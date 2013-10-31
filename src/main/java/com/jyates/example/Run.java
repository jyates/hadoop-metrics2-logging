package com.jyates.example;

import java.util.Random;

import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;

import com.jyates.example.sink.ExampleSink;
import com.jyates.example.source.Instrumentation;
import com.jyates.example.source.MetricSource;

/**
 * Main entry point for this example. Just runs a simple function and uses the metrics logging
 * system to write the output.
 */
public class Run {

  private final Instrumentation metrics = new MetricSource();
  private long count = 0;
  /**
   * @param args
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InterruptedException {
    // run a few times to get some metrics generated
    Run run = new Run();
    for (int i = 0; i < 2; i++) {
      run.doIt(i);
      System.out.println("--- Done doIt ---");
    }
    System.out.println("Counted: " + run.count);

    DefaultMetricsSystem.instance().publishMetricsNow();
    // wait for metrics to be flushed
    ExampleSink.done.await();
  }

  private void doIt(int count) throws InterruptedException {
    // start tracking time
    long start = System.currentTimeMillis();

    // do some random work
    int wait = new Random().nextInt(3000);
    Thread.sleep(wait);

    // finish tracking time
    long end = System.currentTimeMillis();

    // update the metric
    long elapsed = end - start;
    metrics.addPerMethodCallStat("doIt-" + count, elapsed);
    // update our interanl counter too
    this.count += elapsed;
    System.out.println("Elapsed: " + elapsed);
  }

}
