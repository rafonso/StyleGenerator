package org.stylegenerator.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stylegenerator.core.TextFile;

public class TextAnalyzer {

	static Logger logger = LoggerFactory.getLogger(TextAnalyzer.class);

	private TextFile readFile(String filePath) {
		Path file = Paths.get(filePath);
		if (!Files.exists(file)) {
			throw new IllegalArgumentException("It was not possible locate file " + filePath);
		}

		try {
			return new TextFile(file.getFileName().toString(),
					new String(Files.readAllBytes(file), StandardCharsets.ISO_8859_1).trim());
		} catch (IOException e) {
			throw new RuntimeException("Fail fo read file " + filePath, e);
		}
	}

	private Stream<String> dirToFilesNames(String dirPath) {
		Path dir = Paths.get(dirPath);
		if (!Files.exists(dir)) {
			throw new IllegalArgumentException("It was not possible locate directory " + dirPath);
		}
		if (!Files.isDirectory(dir)) {
			throw new IllegalArgumentException(dirPath + " is not a directory");
		}

		List<String> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, "*.txt")) {
			for (Path file : directoryStream) {
				fileNames.add(file.toAbsolutePath().toString());
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (fileNames.isEmpty()) {
			logger.warn("There is no *.txt files in directory " + dirPath);
		}

		return fileNames.stream();
	}

	private List<TextFile> fileNamesStreamToTextFile(Stream<String> fileNamesStream) {
		return fileNamesStream.map(this::readFile).collect(Collectors.toList());
	}

	public List<TextFile> process(List<String> filesPath, List<String> directoriesPaths) {
		List<TextFile> textFilesFromFiles = fileNamesStreamToTextFile(filesPath.stream());
		List<TextFile> textFilesFomDirectories = fileNamesStreamToTextFile(
				directoriesPaths.stream().flatMap(this::dirToFilesNames));

		List<TextFile> textFiles = new ArrayList<>(textFilesFromFiles);
		textFiles.addAll(textFilesFomDirectories);
		
		TextFile textFile = textFiles.get(0);
		
		Pattern pattern = Pattern.compile("((\\n|\\r)+)([^ ]*)");
		
		// CRia uma lista de linhas. Depois tokeniza cada linha e adiciona um '\n' no ultimo token 
		String[] rawTokens = textFile.getText().split(" ");
		for (int i = 0; i < rawTokens.length; i++) {
			String string = rawTokens[i];
			Matcher matcher = pattern.matcher(string);
			if(matcher.matches()) {
				rawTokens[i - 1] = rawTokens[i - 1] + matcher.group(1);
				rawTokens[i] = matcher.group(3);
			}
//			if(string.startsWith("(\\n|\\r)+")) {
//				rawTokens[i - 1] = rawTokens[i - 1] + '\n';
//				rawTokens[i] = string.replaceAll("(\\n|\\r)+", "");
//			} // ((\n|\r)+)([^ ]*)
		}
		
		List<String> tokens = Arrays.asList(rawTokens);
		
		logger.debug(tokens.toString().replaceAll("\\n", "EOL"));

		return textFiles;
	}

}
