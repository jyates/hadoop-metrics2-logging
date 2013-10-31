package com.jyates.example.source;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.metrics2.MetricsCollector;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.MetricsSource;
import org.apache.hadoop.metrics2.lib.Interns;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;

public class MetricSource extends BaseMetricSource implements Instrumentation, MetricsSource {
  public static final String METRIC_NAME = "example-name";
  private static final String CONTEXT = "example";
  // custom name that matches the same metric prefix. Not necessary, but done so we only print our
  // metrics in the sink.
  private static final String CUSTOM_PER_METHOD_CALL_NAME = METRIC_NAME + "dyanmic-method-call";
  private final MutableCounterLong timeGauge;
  private final Map<String, Long> calls = Collections.synchronizedMap(new HashMap<String, Long>());

  public MetricSource() {
    super(METRIC_NAME, CONTEXT, "example metrics system");
    this.timeGauge =
        metricsRegistry.newCounter(METRIC_NAME, "Gauge to measure time of a method", 0L);
  }

  @Override
  public void addPerMethodCallStat(String methodCallId, long time) {
    // increment the total time spent in that method
    this.timeGauge.incr(time);

    // update the per-call value as well
    this.calls.put(methodCallId, time);
  }

  @Override
  public void getMetrics(MetricsCollector metricsCollector, boolean all) {
    // make sure we log the registry
    super.getMetrics(metricsCollector, all);

    // create a builder for the custom metrics
    MetricsRecordBuilder builder =
        metricsCollector.addRecord(Interns.info(METRIC_NAME, "custom, per-call metrics"))
            .setContext(CONTEXT);

    // needed because the metrics system can call us on initialization, before we have instance
    // variables setup, causing an NPE here... it's very odd.
    if (calls == null) {
      return;
    }
    // get all the custom metrics as well
    for (Entry<String, Long> call : calls.entrySet()) {
      builder.addGauge(
        Interns.info(CUSTOM_PER_METHOD_CALL_NAME + call.getKey(), "dynamic, per method value"),
        call.getValue());
    }
  }
}