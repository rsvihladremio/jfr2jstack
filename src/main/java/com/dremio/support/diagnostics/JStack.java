/**
 * Copyright 2022 Dremio
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.support.diagnostics;

import java.nio.file.Path;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

/**
 * The JStack has a number of useful methods to create and use JStack objects
 * but otherwise this is a standard record that contains the text from the thread dump and when it happened
 */
public class JStack {
  private final Instant executionTime;
  private final String text;

  public String getText() {
    return this.text;
  }

  public Instant getExecutionTime() {
    return this.executionTime;
  }

  /**
   * @param executionTime is when the thread dump was run
   * @param text is the actual text of the thread dump
   */
  public JStack(Instant executionTime, String text) {
    this.executionTime = executionTime;
    this.text = text;
  }

  /**
   * Expect that it's been parsed a valid ThreadDump type of event
   *
   * @param e the RecordedEvent from a JFR RecordingFile
   * @return a JStack object parsed from the RecordedEvent
   */
  public static JStack getJStackFromEvent(final RecordedEvent e) {
    Instant startTime = e.getStartTime();
    String result = e.getString("result");
    return new JStack(startTime, result);
  }

  /**
   * toPath generates a file path to write the jstack to
   *
   * @return a path based on the executionTime
   */
  public Path toPath() {
    final String executionTimeStamp = Dates.FormatInstant(executionTime);
    return Path.of(String.format("jstack-%s.txt", executionTimeStamp));
  }
}
