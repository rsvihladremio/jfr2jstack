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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

public class JStackFileWriterTest {
  @Test
  public void testWriteFileToDisk() throws IOException {
    var testDir = Files.createTempDirectory("jfr");
    try {
      var writer = new JStackFileWriterImpl(testDir.toFile());
      var utcDateTime = LocalDateTime.of(2024, 10, 2, 9, 30, 15);
      var execTime = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC")).toInstant();
      var dumpText = "outputText";
      var jstack = new JStack(execTime, dumpText);
      writer.writeToDisk(jstack);
      var fileOutput =
          Files.readAllBytes(Paths.get(testDir.toString(), "jstack-20241002093015.txt"));
      assertEquals(dumpText, new String(fileOutput));
    } finally {
      for (var f : testDir.toFile().listFiles()) {
        f.delete();
      }
      testDir.toFile().delete();
    }
  }
}
