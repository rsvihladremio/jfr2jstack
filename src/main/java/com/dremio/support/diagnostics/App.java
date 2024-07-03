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
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

public class App implements Callable<Integer> {

  @Parameters(index = "0", description = "The jfr to parse")
  private File file;

  @Parameters(index = "1", description = "output folder", defaultValue = "jstacks")
  private File outputFolder;

  @Override
  public Integer call() throws Exception {
    var fileWriter = new JStackFileWriterImpl(outputFolder);
    var reporter = new EventReporter(fileWriter);
    var reader = new JFRReader();
    try {
      reader.analyzeFile(file, reporter::analyzeEvent);
    } catch (Exception e) {
      System.out.println(e);
      return 1;
    }
    return 0;
  }

  public static void main(String... args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}
