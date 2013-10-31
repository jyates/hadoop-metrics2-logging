package com.jyates.example.source;

import org.apache.hadoop.metrics2.MetricsSource;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;

public class MetricSource extends BaseMetricSource implements Instrumentation, MetricsSource {
  public static final String METRIC_NAME = "example-name";
  private static final String CONTEXT = "example";

  private final MutableCounterLong timeGauge;

  public MetricSource() {
    super(METRIC_NAME, CONTEXT, "example metrics system");
    this.timeGauge =
        metricsRegistry.newCounter(METRIC_NAME, "Gauge to measure time of a method", 0L);
  }

  @Override
  public void addTimeStat(long time) {
    this.timeGauge.incr(time);
  }
}