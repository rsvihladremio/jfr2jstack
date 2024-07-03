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

import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.jfr.consumer.RecordedEvent;

public class EventReporter {
  private static final Logger LOGGER = Logger.getLogger(EventReporter.class.getName());
  private final String threadDumpEventName = "jdk.ThreadDump";
  private final JStackFileWriter jStackFileWriter;

  /**
   *
   * @param jStackFileWriter file that persists the jstack to disk
   */
  public EventReporter(JStackFileWriter jStackFileWriter) {
    this.jStackFileWriter = jStackFileWriter;
  }

  /**
   * Will match all jdk.ThreadDump event types, parse them and pass them to
   * a jstackFileWriter
   *
   * @param event event that comes from JFR recording
   */
  public void analyzeEvent(final RecordedEvent event) {
    String eventName = event.getEventType().getName();
    if (eventName.equals(threadDumpEventName)) {
      var jstack = JStack.getJStackFromEvent(event);
      try {
        jStackFileWriter.writeToDisk(jstack);
      } catch (Exception e) {
        LOGGER.log(
            Level.SEVERE,
            String.format("unable to write file for thread dump %s", jstack.getExecutionTime()),
            e);
      }
    }
  }
}
