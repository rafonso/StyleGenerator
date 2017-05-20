/**
 * 
 */
package stylegenerator.stylegeneration.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import stylegenerator.core.Constants;

@Slf4j
public class PathToTextFileFunction implements Function<Path, TextFile> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.function.Function#apply(java.lang.Object)
	 */
	@Override
	public TextFile apply(Path file) {
		if (!Files.exists(file)) {
			throw new IllegalArgumentException("It was not possible locate file " + file.toAbsolutePath().toString());
		}

		try {
			log.debug("Reading file {}", file);
			return new TextFile(file.getFileName().toString(),
					new String(Files.readAllBytes(file), Constants.CHARSET).trim());
		} catch (IOException e) {
			throw new RuntimeException("Fail fo read file " + file.toAbsolutePath().toString(), e);
		}
	}

}
