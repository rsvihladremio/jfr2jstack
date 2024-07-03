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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import org.junit.jupiter.api.Test;

public class EventReporterTest {
  @Test
  public void testEventIsRun() throws URISyntaxException, IOException {
    var mockJStackFileWriter = mock(JStackFileWriter.class);
    var eventReporter = new EventReporter(mockJStackFileWriter);

    var resource = this.getClass().getResource("/flight_recording.jfr");
    assertNotNull(resource);
    var jfrFile = new File(resource.toURI());
    assertTrue(jfrFile.exists());
    try (var recording = new RecordingFile(jfrFile.toPath())) {
      while (recording.hasMoreEvents()) {
        RecordedEvent e = recording.readEvent();
        eventReporter.analyzeEvent(e);
      }
    }
    // expect to find 2 events in the jfr recording so we use the mock to see if writeToDisk was
    // called two times
    verify(mockJStackFileWriter, times(2)).writeToDisk(any(JStack.class));
  }
}
