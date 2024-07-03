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

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import jdk.jfr.consumer.RecordingFile;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

public class App implements Callable<Integer> {

  @Parameters(index = "0", description = "The jfr to parse")
  private File file;

  @Parameters(index = "1", description = "output folder", defaultValue = "jstacks")
  private File outputFolder;

  private final String threadDumpEventName = "jdk.ThreadDump";

  @Override
  public Integer call() throws Exception {
    final Map<Instant, String> jstacks = new HashMap<>();
    try (var recordingFile = new RecordingFile(file.toPath())) {
      while (recordingFile.hasMoreEvents()) {
        var e = recordingFile.readEvent();
        String eventName = e.getEventType().getName();
        if (eventName.equals(threadDumpEventName)) {
          Instant startTime = e.getStartTime();
          String result = e.getString("result");
          jstacks.put(startTime, result);
        }
      }
    }
    for (Map.Entry<Instant, String> jstack : jstacks.entrySet()) {
      try (final BufferedWriter writer =
          Files.newBufferedWriter(
              Paths.get(
                  outputFolder.toPath().toAbsolutePath().toString(),
                  "jstack-"
                      + jstack.getKey().atZone(ZoneId.of("UTC")).format(dateTimeFormatter)
                      + ".txt"))) {
        writer.append(jstack.getValue());
        writer.flush();
      }
    }
    return 0;
  }

  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");

  public static void main(String... args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}
