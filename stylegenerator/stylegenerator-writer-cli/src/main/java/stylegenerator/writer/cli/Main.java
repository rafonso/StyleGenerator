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

		return options;
	}

	private static Integer commandToInteger(CommandLine line, String parameter) {
		if (line.hasOption(parameter)) {
			return Integer.parseInt(line.getOptionValue(parameter));
		}

		return null;
	}

	private static StyleParameters commandLineToStyleParameters(CommandLine line) {
		StyleParameters styleParameters = StyleParameters.builder() //
				.waitForEndOfText(line.hasOption(WAIT_FOR_END_OF_TEXT)) //
				.quantityOfWords(commandToInteger(line, WORDS_QUANTITY_PARAMETER))
				.waitForEndOfPrhase(line.hasOption(WAIT_FOR_END_OF_PHRASE_PARAMETER)) //
				.quantityOfPhrases(commandToInteger(line, PHRASES_QUANTITY_PARAMETER))
				.quantityOfLines(commandToInteger(line, LINES_QUANTITY_PARAMETER)) //
				.build();
		return styleParameters;
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
		CommandLineParser parser = new DefaultParser();
		Options options = getOptions();

		try {
			CommandLine line = parser.parse(options, args);

			if (!line.hasOption(FILE_PARAMETER)) {
				throw new IllegalArgumentException("Please provide the style file (*.style.json extension)");
			}

			StyleParameters styleParameters = commandLineToStyleParameters(line);

			log.debug(styleParameters.toString());

			List<Sentence> sentences = readStyleFile(line.getOptionValue(FILE_PARAMETER));

			// log.debug(sentences.toString());

			TextGenerator textGenerator = new TextGenerator();

			String text = textGenerator.generateText(sentences, styleParameters);

			log.debug(text);

			log.info("Finished");
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
