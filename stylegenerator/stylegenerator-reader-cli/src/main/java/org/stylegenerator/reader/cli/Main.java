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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stylegenerator.reader.TextAnalyzer;

import stylegenerator.core.StyleParameters;
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
		options.addOption("c", "coherence", true, "Coherence level");

		return options;
	}
	
	private static StyleParameters getParameters(CommandLine line) {
		StyleParameters parameters = new StyleParameters();
		
		parameters.setCoherence(Integer.valueOf(line.getOptionValue("c", "3")));
		
		return parameters;
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
			
			StyleParameters parameters = getParameters(line);

			TextAnalyzer analyzer = new TextAnalyzer();

			List<TextFile> textFiles = analyzer.process(filesNames, directoriesNames, parameters);

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
