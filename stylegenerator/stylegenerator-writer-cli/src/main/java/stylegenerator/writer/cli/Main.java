package stylegenerator.writer.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import stylegenerator.core.Constants;
import stylegenerator.core.Sentence;
import stylegenerator.core.StyleParameters;
import stylegenerator.textgeneration.TextGenerator;

@Slf4j
public class Main {

	private static final String FILE_PARAMETER = "f";
	private static final String WAIT_FOR_END_OF_TEXT = "eot";
	private static final String WORDS_QUANTITY_PARAMETER = "w";
	private static final String WAIT_FOR_END_OF_PHRASE_PARAMETER = "weop";
	private static final String PHRASES_QUANTITY_PARAMETER = "p";
	private static final String LINES_QUANTITY_PARAMETER = "l";
	private static final String HELPER_PARAMETER = "h";

	private static void showOptions(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Main", options);
	}

	private static Options getOptions() {
		Options options = new Options();

		options.addOption(FILE_PARAMETER, true, "Style File to be read (*.style.json extension)");
		options.addOption(WAIT_FOR_END_OF_TEXT, false, "Generates until the first End of Text");
		options.addOption(WORDS_QUANTITY_PARAMETER, true, "Quantity of words to be generated");
		options.addOption(WAIT_FOR_END_OF_PHRASE_PARAMETER, false,
				"Wait for end of phrase if Quantity of words is chosen");
		options.addOption(PHRASES_QUANTITY_PARAMETER, true, "Quantity of phrases to be generated");
		options.addOption(LINES_QUANTITY_PARAMETER, true, "Quantity of lines to be generated");
		options.addOption(HELPER_PARAMETER, false, "Prints Usage");

		return options;
	}

	private static Integer commandToInteger(CommandLine line, String parameter) {
		Integer result = null;
		if (line.hasOption(parameter)) {
			String parameterValue = line.getOptionValue(parameter);
			try {
				result = Integer.parseInt(parameterValue);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid value for parameter " + parameter + ": " + parameterValue);
			}
		}

		return result;
	}

	private static StyleParameters commandLineToStyleParameters(CommandLine line) {
		return StyleParameters.builder() //
				.waitForEndOfText(line.hasOption(WAIT_FOR_END_OF_TEXT)) //
				.quantityOfWords(commandToInteger(line, WORDS_QUANTITY_PARAMETER))
				.waitForEndOfPrhase(line.hasOption(WAIT_FOR_END_OF_PHRASE_PARAMETER)) //
				.quantityOfPhrases(commandToInteger(line, PHRASES_QUANTITY_PARAMETER))
				.quantityOfLines(commandToInteger(line, LINES_QUANTITY_PARAMETER)) //
				.build();
	}

	private static List<Sentence> readStyleFile(String filePath)
			throws JsonParseException, JsonMappingException, IOException {
		if (!filePath.endsWith(Constants.FILE_STYLE_EXTENSION)) {
			throw new IllegalArgumentException(
					"Style file must have *" + Constants.FILE_STYLE_EXTENSION + " extension");
		}

		File styleFile = new File(filePath);
		if (!styleFile.exists()) {
			throw new IllegalArgumentException("Style file " + filePath + " not found");
		}
		if (!styleFile.isFile()) {
			throw new IllegalArgumentException(filePath + " is not a File");
		}

		ObjectMapper mapper = new ObjectMapper();

		TypeReference<List<Sentence>> typeReference = new TypeReference<List<Sentence>>() {
		};

		@SuppressWarnings("unchecked")
		List<Sentence> sentences = (List<Sentence>) mapper.readValue(styleFile, typeReference);

		return sentences;
	}

	public static void main(String[] args) {
		log.info("Starting ...");
		long t0 = System.currentTimeMillis();

		CommandLineParser parser = new DefaultParser();
		Options options = getOptions();
		String text = null;

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(HELPER_PARAMETER)) {
				showOptions(options);
				return;
			}
			if (!line.hasOption(FILE_PARAMETER)) {
				throw new IllegalArgumentException("Please provide the style file (*.style.json extension)");
			}

			StyleParameters styleParameters = commandLineToStyleParameters(line);
			log.debug(styleParameters.toString());

			log.info("Reading Style file.");
			List<Sentence> sentences = readStyleFile(line.getOptionValue(FILE_PARAMETER));

			TextGenerator textGenerator = new TextGenerator(styleParameters);

			log.info("Generating text");
			text = textGenerator.generateText(sentences);
			log.info("Text generated");

			// TODO: IF to generate output file
		} catch (ParseException e) {
			log.error("Invalid Command Line: " + e.getMessage(), e);
			showOptions(options);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			showOptions(options);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		log.info("Finished. Time: {} ms", (System.currentTimeMillis() - t0));

		if (text != null) {
			System.out.println(text);
		}
	}

}
