/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jfr2jstack;

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
    boolean startParsing = true;
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
      final BufferedWriter writer =
          Files.newBufferedWriter(
              Paths.get(
                  outputFolder.toPath().toAbsolutePath().toString(),
                  "jstack-"
                      + jstack.getKey().atZone(ZoneId.of("UTC")).format(dateTimeFormatter)
                      + ".txt"));
    }
    return 0;
  }

  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
  private final DateTimeFormatter jfrDateFmt = DateTimeFormatter.ofPattern("yyyyMM ddhhmm");

  public static void main(String... args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}
