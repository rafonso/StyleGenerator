package org.stylegenerator.reader.cli;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import stylegenerator.core.Constants;
import stylegenerator.core.Sentence;
import stylegenerator.stylegeneration.SentencesMerger;
import stylegenerator.stylegeneration.TextFileToSentences;
import stylegenerator.stylegeneration.io.DirPathToFilesPath;
import stylegenerator.stylegeneration.io.PathToTextFileFunction;

@Slf4j
public class Main {

	private static final String COHERENCE_PARAMETER = "c";
	private static final String DIR_PARAMETER = "d";
	private static final String FILE_PARAMETER = "f";
	private static final String OUTPUT_DIR_PARAMETER = "od";
	private static final String OUTPUT_FILE_PARAMETER = "of";
	private static final String HELPER_PARAMETER = "h";

	private static void showOptions(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Main", options);
	}

	private static Options getOptions() {
		Options options = new Options();

		options.addOption(FILE_PARAMETER, true, "File to be read (*.txt extension)");
		options.addOption(DIR_PARAMETER, true, "Directory to read. All *.txt files will be read");
		options.addOption(COHERENCE_PARAMETER, true, "Coherence level (Default = 3)");
		options.addOption(OUTPUT_DIR_PARAMETER, true, "Output directory (default current directory)");
		options.addOption(OUTPUT_FILE_PARAMETER, true,
				"Output file name, with extension '*.style.json' (default pattern yyyy-MM-dd-HH-MM-ss)");
		options.addOption(HELPER_PARAMETER, false, "Prints Usage");

		return options;
	}

	private static void validateParameters(CommandLine line) {
		if (!line.hasOption(FILE_PARAMETER) && !line.hasOption(DIR_PARAMETER)) {
			throw new IllegalArgumentException("Please provide at least one file or directory");
		}
	}

	private static File prepareOutputFile(String dirPath, String fileName, int coherence) {
		File outputDir = (dirPath == null) ? new File(".") : new File(dirPath);
		if (!outputDir.exists() || !outputDir.isDirectory()) {
			throw new IllegalArgumentException(
					"Output directory " + dirPath + " does not correspond to a real directory");
		}

		String outputFileName = (fileName == null)
				? LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DEFAULT_FILE_NAME_PATTERN))
				: fileName;

		return new File(outputDir, outputFileName + "." + coherence + Constants.FILE_STYLE_EXTENSION);
	}

	public static void main(String[] args) {
		System.setProperty("file.encoding", Constants.CHARSET.name());

		long t0 = System.currentTimeMillis();

		Options options = getOptions();

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(HELPER_PARAMETER)) {
				showOptions(options);
				return;
			}
			validateParameters(line);

			log.info("Starting ...");
			Integer coherence = Integer.valueOf(line.getOptionValue(COHERENCE_PARAMETER, "3"));
			File outputFile = prepareOutputFile(line.getOptionValue(OUTPUT_DIR_PARAMETER),
					line.getOptionValue(OUTPUT_FILE_PARAMETER), coherence);

			List<String> filesNames = line.hasOption(FILE_PARAMETER)
					? Arrays.asList(line.getOptionValues(FILE_PARAMETER)) : Collections.<String> emptyList();
			List<String> directoriesNames = line.hasOption(DIR_PARAMETER)
					? Arrays.asList(line.getOptionValues(DIR_PARAMETER)) : Collections.<String> emptyList();

			log.info("Processing files. Coherence Level: {}", coherence);
			List<Sentence> sentences = Stream //
					.concat(filesNames.stream(), directoriesNames.stream().flatMap(new DirPathToFilesPath())) //
					.map(Paths::get) //
					.map(new PathToTextFileFunction()) //
					.map(new TextFileToSentences(coherence)) //
					.collect(new SentencesMerger());
			log.info("Files Processed. Numeber of sentences: {}", sentences.size());

			log.info("Recording Style File");
			ObjectMapper mapper = new ObjectMapper();
			mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, sentences);
			log.info("Style file {} Created", outputFile.getAbsolutePath());

			log.info("Finished. Time: {} ms", (System.currentTimeMillis() - t0));
		} catch (ParseException e) {
			log.error("Invalid Command Line: " + e.getMessage(), e);
			showOptions(options);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			showOptions(options);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
