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
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import jdk.jfr.consumer.RecordedEvent;
import org.junit.jupiter.api.Test;

public class JStackTest {
  @Test
  public void testJStackGetters() {
    var execTime = Instant.ofEpochMilli(1720014464000L);
    var dumpText = "this is a thread dump";
    var jstack = new JStack(execTime, dumpText);
    assertEquals(dumpText, jstack.getText());
    assertEquals(execTime, jstack.getExecutionTime());
  }

  @Test
  public void testToPath() {
    var utcDateTime = LocalDateTime.of(2024, 10, 2, 9, 30, 15);
    var execTime = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC")).toInstant();
    var dumpText = "";
    var jstack = new JStack(execTime, dumpText);
    assertEquals(Path.of("jstack-20241002093015.txt"), jstack.toPath());
  }

  @Test
  public void testGetStackFromEvent() throws Exception {
    JFRReader reader = new JFRReader();
    final List<JStack> jstacks = new ArrayList<>();
    Consumer<RecordedEvent> consumer =
        (e) -> {
          if (e.getEventType().getName().equals("jdk.ThreadDump"))
            jstacks.add(JStack.getJStackFromEvent(e));
        };
    var resource = this.getClass().getResource("/flight_recording.jfr");
    assertNotNull(resource);
    var jfrFile = new File(resource.toURI());
    assertTrue(jfrFile.exists());
    reader.analyzeFile(jfrFile, consumer);
    assertEquals(2, jstacks.size());
    var jstack1 = jstacks.get(0);
    assertEquals(1720020487951L, jstack1.getExecutionTime().toEpochMilli());
    assertTrue(
        jstack1
            .getText()
            .contains("Full thread dump OpenJDK 64-Bit Server VM (17.0.11+9 mixed mode, sharing):"),
        jstack1.getText());
    var jstack2 = jstacks.get(1);
    assertEquals(1720020511933L, jstack2.getExecutionTime().toEpochMilli());
    assertTrue(
        jstack1
            .getText()
            .contains("Full thread dump OpenJDK 64-Bit Server VM (17.0.11+9 mixed mode, sharing):"),
        jstack2.getText());
  }
}
