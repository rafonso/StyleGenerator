package stylegenerator.textgeneration.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import stylegenerator.textgeneration.TextGenerator;
import stylegenerator.textgeneration.TextParameter;

@Slf4j
public class TextGeneration {

	private static final String STYLE_FILE_DESCRIPTION = "*.nn" + Constants.GENERATED_FILE_EXTENSION
			+ " extension, where 'nn' is the coherence level";
	private static final String FILE_PARAMETER = "f";
	private static final String WAIT_FOR_END_OF_TEXT = "eot";
	private static final String WORDS_QUANTITY_PARAMETER = "w";
	private static final String WAIT_FOR_END_OF_PHRASE_PARAMETER = "weop";
	private static final String PHRASES_QUANTITY_PARAMETER = "p";
	private static final String LINES_QUANTITY_PARAMETER = "l";
	private static final String OUTPUT_DIR_PARAMETER = "od";
	private static final String OUTPUT_FILE_PARAMETER = "of";
	private static final String HELPER_PARAMETER = "h";

	private static final String STYLE_FILE_PATTERN = ".*(\\\\|/)([^.]+)\\.(\\d+)"
			+ Constants.FILE_STYLE_EXTENSION.replaceAll("\\.", "\\\\.") + "$";

	private static void showOptions(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Main", options);
	}

	private static Options getOptions() {
		Options options = new Options();

		options.addOption(FILE_PARAMETER, true, "Style File to be read (" + STYLE_FILE_DESCRIPTION + ")");
		options.addOption(WAIT_FOR_END_OF_TEXT, false, "Generates until the first End of Text");
		options.addOption(WORDS_QUANTITY_PARAMETER, true, "Quantity of words to be generated");
		options.addOption(WAIT_FOR_END_OF_PHRASE_PARAMETER, false,
				"Wait for end of phrase if Quantity of words is chosen");
		options.addOption(PHRASES_QUANTITY_PARAMETER, true, "Quantity of phrases to be generated");
		options.addOption(LINES_QUANTITY_PARAMETER, true, "Quantity of lines to be generated");
		options.addOption(OUTPUT_DIR_PARAMETER, true, "Output directory (default current directory)."
				+ " The output file will be generated only this or " + OUTPUT_FILE_PARAMETER + " where passed.");
		options.addOption(OUTPUT_FILE_PARAMETER, true,
				"Output file name, with extension " + Constants.GENERATED_FILE_EXTENSION
						+ " (default pattern [STYLE FILE NAME].yyyy-MM-dd-HH-MM-ss)."
						+ " The output file will be generated only this or " + OUTPUT_DIR_PARAMETER + " where passed.");
		options.addOption(HELPER_PARAMETER, false, "Prints Usage");

		return options;
	}

	private static void validateParameters(CommandLine line) {
		if (!line.hasOption(FILE_PARAMETER)) {
			throw new IllegalArgumentException("Please provide the style file (" + STYLE_FILE_DESCRIPTION + ")");
		}
		if (!line.getOptionValue(FILE_PARAMETER).matches(STYLE_FILE_PATTERN)) {
			throw new IllegalArgumentException("Style file shoud have " + STYLE_FILE_DESCRIPTION + ". But his name was "
					+ line.getOptionValue(FILE_PARAMETER));
		}
		if (!line.hasOption(WAIT_FOR_END_OF_TEXT) //
				&& !line.hasOption(LINES_QUANTITY_PARAMETER) //
				&& !line.hasOption(PHRASES_QUANTITY_PARAMETER) //
				&& !line.hasOption(WORDS_QUANTITY_PARAMETER)) {
			throw new IllegalArgumentException(
					String.format("Please provide at least ont of these parameters: '%s', '%s', '%s', '%s'", //
							WAIT_FOR_END_OF_TEXT, //
							LINES_QUANTITY_PARAMETER, //
							PHRASES_QUANTITY_PARAMETER, //
							WORDS_QUANTITY_PARAMETER));
		}
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

	private static TextParameter commandLineToStyleParameter(CommandLine line) {
		return TextParameter.builder() //
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

	private static Path getOutputFile(CommandLine line) {
		String styleFileParameter = line.getOptionValue(FILE_PARAMETER);
		Matcher styleFileMatcher = Pattern.compile(STYLE_FILE_PATTERN).matcher(styleFileParameter);
		styleFileMatcher.matches();
		String generatedFileNameTemination = "."
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DEFAULT_FILE_NAME_PATTERN)) + "."
				+ styleFileMatcher.group(3) + Constants.GENERATED_FILE_EXTENSION;

		String generatedDirPath = line.getOptionValue(OUTPUT_DIR_PARAMETER, ".");
		String generatedFileName = line.getOptionValue(OUTPUT_FILE_PARAMETER, styleFileMatcher.group(2))
				+ generatedFileNameTemination;

		File generatedDir = new File(generatedDirPath);
		if (!generatedDir.exists() || !generatedDir.isDirectory()) {
			throw new IllegalArgumentException("Output directory " + generatedDir.getName() + " is not valid");
		}

		return new File(generatedDir, generatedFileName).toPath();
	}

	private static boolean createOutputFile(CommandLine line, String text) throws IOException {
		if (!line.hasOption(OUTPUT_DIR_PARAMETER) && !line.hasOption(OUTPUT_FILE_PARAMETER)) {
			return false;
		}

		Path outputFile = getOutputFile(line);

		log.debug("Creating output File");
		Files.write(outputFile, text.getBytes(Constants.CHARSET));
		log.debug("Output File {} created", outputFile.getFileName());

		return true;
	}

	public static void main(String[] args) {
		System.setProperty("file.encoding", Constants.CHARSET.name());
		
		long t0 = System.currentTimeMillis();

		Options options = getOptions();
		String text = null;
		boolean outputFileCreated = false;

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(HELPER_PARAMETER)) {
				showOptions(options);
				return;
			}
			validateParameters(line);

			log.info("Starting ...");
			TextParameter parameter = commandLineToStyleParameter(line);
			log.debug(parameter.toString());

			String fileParameter = line.getOptionValue(FILE_PARAMETER);
			log.info("Reading Style file {}", fileParameter);
			List<Sentence> sentences = readStyleFile(fileParameter);

			TextGenerator textGenerator = new TextGenerator(parameter);

			log.info("Generating text");
			text = textGenerator.generateText(sentences);
			log.info("Text generated");

			outputFileCreated = createOutputFile(line, text);

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

		if (text != null && !outputFileCreated) {
			System.out.println(text);
		}
	}

}
