package org.stylegenerator.reader.cli;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import stylegenerator.core.Sentence;
import stylegenerator.reader.TextsToStyle;

@Slf4j
public class Main {

	private static final String COHERENCE_PARAMETER = "c";
	private static final String DIR_PARAMETER = "d";
	private static final String FILE_PARAMETER = "f";

	private static void showOptions(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Main", options);
	}

	private static Options getOptions() {
		Options options = new Options();

		options.addOption(FILE_PARAMETER, true, "File to be read (*.txt extension)");
		options.addOption(DIR_PARAMETER, true, "Directory to read. All *.txt files will be read");
		options.addOption(COHERENCE_PARAMETER, true, "Coherence level (Default = 3)");

		return options;
	}

	public static void main(String[] args) {
		log.info("Starting ...");
		CommandLineParser parser = new DefaultParser();
		Options options = getOptions();

		try {
			CommandLine line = parser.parse(options, args);

			if (!line.hasOption(FILE_PARAMETER) && !line.hasOption(DIR_PARAMETER)) {
				log.warn("Please provide at least one file or directory");
				showOptions(options);
				return;
			}

			List<String> filesNames = line.hasOption(FILE_PARAMETER)
					? Arrays.asList(line.getOptionValues(FILE_PARAMETER)) : Collections.<String> emptyList();
			List<String> directoriesNames = line.hasOption(DIR_PARAMETER)
					? Arrays.asList(line.getOptionValues(DIR_PARAMETER)) : Collections.<String> emptyList();

			log.debug(filesNames.toString());
			log.debug(directoriesNames.toString());

			Integer coherence = Integer.valueOf(line.getOptionValue(COHERENCE_PARAMETER, "3"));

			TextsToStyle analyzer = new TextsToStyle();

			List<Sentence> sentences = analyzer.process(filesNames, directoriesNames, coherence);

			sentences.forEach(tf -> log.debug(tf.toString()));

			ObjectMapper mapper = new ObjectMapper();

			String json = mapper.writeValueAsString(sentences);

			log.debug(json);

			log.info("Finished");
		} catch (ParseException e) {
			log.error("Invalid Command Line: " + e.getMessage(), e);
			showOptions(options);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}
}
