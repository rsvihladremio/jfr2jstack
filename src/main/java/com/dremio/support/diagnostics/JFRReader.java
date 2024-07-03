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

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

/**
 * reads all the events from a jfr file and passes an analyzer to each
 */
public class JFRReader {
  /**
   *
   * @param file JFR recording to analyze
   * @param analyzer analysis to run against each event
   * @throws IOException when we are unable to read the jfr file
   */
  public void analyzeFile(final File file, final Consumer<RecordedEvent> analyzer)
      throws IOException {
    try (final var recordingFile = new RecordingFile(file.toPath())) {
      // loop until we run out of events
      while (recordingFile.hasMoreEvents()) {
        // read the event. It is up to the analyzer to check the event type
        // and do the right thing for that given event.
        final RecordedEvent e = recordingFile.readEvent();
        analyzer.accept(e);
      }
    }
  }
}
