package com.jyates.example.source;

/**
 * Generic interface to hide details of instrumenting. Not needed at this scale, but useful for
 * larger projects that might want to
 * <ol>
 * <li>Swap metrics systems via configuration</li>
 * <li>Use different metrics systems</li>
 * </ol>
 */
public interface Instrumentation {

  public void addTimeStat(long time);
}
