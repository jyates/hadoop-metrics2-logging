package com.jyates.example.sink;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.metrics2.MetricsSink;
import org.apache.hadoop.metrics2.MetricsTag;

import com.jyates.example.source.MetricSource;

public class ExampleSink implements MetricsSink {

  /**
   * Latch <b>just for this example</b>, to be sure that we have written the metric (since metrics
   * are on a timer).
   */
  public static final CountDownLatch done = new CountDownLatch(1);

  @Override
  public void init(SubsetConfiguration conf) {
    /*
     * From the docs (http://hadoop.apache.org/docs/current/api/index.html?org/apache/hadoop/metrics
     * /package-summary.html): The configuration object for the sink instance with prefix removed.
     * So you can get any sink specific configuration using the usual get* method.
     */
    System.out.println("Found property: " + conf.getString("someproperty"));
  }

  @Override
  public void putMetrics(MetricsRecord record) {
    // get the hostname so we can print that info
    String hostname = null;
    for (MetricsTag tag : record.tags()) {
      if (tag.name().equals("Hostname")) {
        hostname = tag.value();
        break;
      }
    }
    System.out.println("For host: '" + hostname + "' tracked metrics:");
    for(AbstractMetric metric: record.metrics()){
      //just print the metric we care about
      if (metric.name().startsWith(MetricSource.METRIC_NAME)) {
        System.out.println("\t" + metric);
      }
    }

    
  }

  @Override
  public void flush() {
    /*
     * From the docs (http://hadoop.apache.org/docs/current/api/index.html?org/apache/hadoop/metrics
     * /package-summary.html): This method is called for each update cycle, which may involve more
     * than one record. The sink should try to flush any buffered metrics to its backend upon the
     * call. But it's not required that the implementation is synchronous.
     */
    done.countDown();
  }
}