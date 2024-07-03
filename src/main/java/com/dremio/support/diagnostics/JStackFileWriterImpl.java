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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * JStackFileWriter writes any JStack object passed to it to a base directory
 */
public class JStackFileWriterImpl implements JStackFileWriter {

  private final File outputDir;

  /**
   *
   * @param outputDir is the base directory we use to write all JStack files
   */
  public JStackFileWriterImpl(final File outputDir) {
    this.outputDir = outputDir;
  }

  /**
   * writes a jstack that was parsed to disk based on the output dir configured for the JStackFileWriter
   * @param jstack JStack object to flush to disk based on the executionTime and the text for the jstack
   * @throws IOException
   */
  public void writeToDisk(final JStack jstack) throws IOException {
    final Path outputPath =
        Paths.get(this.outputDir.toPath().toString(), jstack.toPath().toString());
    try (final BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      writer.append(jstack.getText());
      writer.flush();
    }
  }
}
