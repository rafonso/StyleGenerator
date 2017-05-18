/**
 * 
 */
package org.stylegenerator.reader.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import stylegenerator.core.Constants;
import stylegenerator.core.TextFile;

/**
 * @author rucaf_000
 *
 */
public class PathToTextFileFunction implements Function<Path, TextFile> {

	/* (non-Javadoc)
	 * @see java.util.function.Function#apply(java.lang.Object)
	 */
	@Override
	public TextFile apply(Path file) {
		if (!Files.exists(file)) {
			throw new IllegalArgumentException("It was not possible locate file " + file.toAbsolutePath().toString());
		}

		try {
			return new TextFile(file.getFileName().toString(),
					new String(Files.readAllBytes(file), Constants.CHARSET).trim());
		} catch (IOException e) {
			throw new RuntimeException("Fail fo read file " + file.toAbsolutePath().toString(), e);
		}
	}

}
