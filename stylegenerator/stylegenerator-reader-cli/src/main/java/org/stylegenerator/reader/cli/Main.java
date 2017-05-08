package org.stylegenerator.reader.cli;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stylegenerator.core.TextFile;

public class Main {

	static Logger logger = LoggerFactory.getLogger(Main.class);

	private static void showOptions(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Main", options);
	}

	private static Options getOptions() {
		Options options = new Options();

		options.addOption("f", "file", true, "File to be read (*.txt extension)");
		options.addOption("d", "dir", true, "Directory to read. All *.txt files will be read");

		return options;
	}

	private static TextFile readFile(String filePath) {
		Path file = Paths.get(filePath);
		if (!Files.exists(file)) {
			throw new IllegalArgumentException("It was not possible locate file " + filePath);
		}

		try {
			return new TextFile(file.getFileName().toString(),
					new String(Files.readAllBytes(file), StandardCharsets.ISO_8859_1));
		} catch (IOException e) {
			throw new RuntimeException("Fail fo read file " + filePath, e);
		}
	}

	private static Stream<String> dirToFilesNames(String dirPath) {
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

	private static List<TextFile> fileNamesStreamToTextFile(Stream<String> fileNamesStream) {
		return fileNamesStream.map(Main::readFile).collect(Collectors.toList());
	}

	public static void main(String[] args) {
		logger.info("Starting ...");
		CommandLineParser parser = new DefaultParser();
		Options options = getOptions();

		try {
			CommandLine line = parser.parse(options, args);

			if (!line.hasOption("f") && !line.hasOption("d")) {
				logger.warn("Please provide at least one file or directory");
				showOptions(options);
				return;
			}

			List<String> filesNames = line.hasOption("f") ? Arrays.asList(line.getOptionValues("f"))
					: Collections.<String> emptyList();
			List<String> directoriesNames = line.hasOption("d") ? Arrays.asList(line.getOptionValues("d"))
					: Collections.<String> emptyList();

			logger.debug(filesNames.toString());
			logger.debug(directoriesNames.toString());

			List<TextFile> textFilesFromFiles = fileNamesStreamToTextFile(filesNames.stream());
			List<TextFile> textFilesFomDirectories = fileNamesStreamToTextFile(
					directoriesNames.stream().flatMap(Main::dirToFilesNames));

			List<TextFile> textFiles = new ArrayList<>(textFilesFromFiles);
			textFiles.addAll(textFilesFomDirectories);

			textFiles.forEach(tf -> logger.debug(tf.toString()));
			logger.info("Finished");
		} catch (ParseException e) {
			logger.error("Invalid Command Line", e);
			showOptions(options);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}
}
