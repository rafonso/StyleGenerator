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

			List<String> files = line.hasOption("f") ? Arrays.asList(line.getOptionValues("f"))
					: Collections.<String> emptyList();
			List<String> directories = line.hasOption("d") ? Arrays.asList(line.getOptionValues("d"))
					: Collections.<String> emptyList();

			logger.debug(files.toString());
			logger.debug(directories.toString());
			logger.info("Finished");
		} catch (ParseException e) {
			logger.error("Invalid Command Line", e);
			showOptions(options);
		}
		
	}
}
