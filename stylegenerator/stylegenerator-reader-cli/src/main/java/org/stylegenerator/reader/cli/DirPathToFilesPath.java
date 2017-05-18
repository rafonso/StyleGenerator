package org.stylegenerator.reader.cli;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirPathToFilesPath implements Function<String, Stream<String>>{

	private void validarDir(String dirPath, Path dir) {
		if (!Files.exists(dir)) {
			throw new IllegalArgumentException("It was not possible locate directory " + dirPath);
		}
		if (!Files.isDirectory(dir)) {
			throw new IllegalArgumentException(dirPath + " is not a directory");
		}
	}

	private List<String> getFilesNames(Path dir) {
		List<String> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, "*.txt")) {
			for (Path file : directoryStream) {
				fileNames.add(file.toAbsolutePath().toString());
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return fileNames;
	}

	private void verifyFilesNames(List<String> fileNames, String dirPath) {
		if (fileNames.isEmpty()) {
			log.warn("There is no *.txt files in directory " + dirPath);
		}
	}

	@Override
	public Stream<String> apply(String dirPath) {
		Path dir = Paths.get(dirPath);
		validarDir(dirPath, dir);

		List<String> fileNames = getFilesNames(dir);
		verifyFilesNames(fileNames, dirPath);

		return fileNames.stream();
	}

}
