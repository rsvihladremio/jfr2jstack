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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import jdk.jfr.consumer.RecordedEvent;
import org.junit.jupiter.api.Test;

public class JFRReaderTest {
  @Test
  public void testJFRReader() throws URISyntaxException, IOException {
    JFRReader reader = new JFRReader();
    var resource = this.getClass().getResource("/flight_recording.jfr");
    assertNotNull(resource);
    var jfrFile = new File(resource.toURI());
    assertTrue(jfrFile.exists());
    final List<RecordedEvent> events = new ArrayList<>();
    reader.analyzeFile(jfrFile, (e) -> events.add(e));
    assertEquals(10559, events.size());
  }
}
