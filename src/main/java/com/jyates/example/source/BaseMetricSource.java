package com.jyates.example.source;

import org.apache.hadoop.metrics2.MetricsCollector;
import org.apache.hadoop.metrics2.MetricsSource;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;

public abstract class BaseMetricSource implements MetricsSource {
  public static final String METRICS_SYSTEM_NAME = "example";
  protected final MetricsRegistry metricsRegistry;

  public BaseMetricSource(String name, String context, String description) {
    metricsRegistry = new MetricsRegistry(name).setContext(context);
    MetricsSystem system = DefaultMetricsSystem.initialize(METRICS_SYSTEM_NAME);

    // Register this instance.
    // For right now, we ignore the MBean registration issues that show up in DEBUG logs. Basically,
    // we need a Jmx MBean compliant name. We'll get to a better example in another project.
    DefaultMetricsSystem.instance().register(context, description, this);
  }

  @Override
  public void getMetrics(MetricsCollector metricsCollector, boolean all) {
    metricsRegistry.snapshot(metricsCollector.addRecord(metricsRegistry.info()), all);
  }
}