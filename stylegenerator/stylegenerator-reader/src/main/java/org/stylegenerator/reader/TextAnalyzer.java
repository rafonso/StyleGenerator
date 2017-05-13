package org.stylegenerator.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stylegenerator.core.SentenceSequences;
import stylegenerator.core.StyleParameters;
import stylegenerator.core.TextFile;

public class TextAnalyzer {

	static Logger logger = LoggerFactory.getLogger(TextAnalyzer.class);

	private static final String EOL = System.getProperty("line.separator");

	private Pattern eolSpacePattern = Pattern.compile("^([^\\r\\n]*\\r\\n) +(.*)$");

	/*
	 * Getting Text Files - BEGIN
	 */

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

	private List<TextFile> getTextFiles(List<String> filesPath, List<String> directoriesPaths) {
		List<TextFile> textFilesFromFiles = fileNamesStreamToTextFile(filesPath.stream());
		List<TextFile> textFilesFomDirectories = fileNamesStreamToTextFile(
				directoriesPaths.stream().flatMap(this::dirToFilesNames));

		List<TextFile> textFiles = new ArrayList<>();
		textFiles.addAll(textFilesFromFiles);
		textFiles.addAll(textFilesFomDirectories);
		return textFiles;
	}

	/*
	 * Getting Text Files - END
	 */

	/*
	 * Getting Setence Sequences - BEGIN
	 */

	private String removeEolSpace(String token) {
		Matcher matcher = eolSpacePattern.matcher(token);

		return matcher.matches() ? (matcher.group(1) + matcher.group(2)) : token;
	}

	private List<SentenceSequences> parseText(List<String> rawTokens, int coherence) {
		Queue<String> sentence = new LinkedList<>(rawTokens.subList(0, coherence));
		List<SentenceSequences> sentences = new ArrayList<>();

		for (int i = coherence; i < rawTokens.size(); i++) {
			String currSentence = removeEolSpace(sentence.stream().collect(Collectors.joining(" ")));
			String sequence = rawTokens.get(i);

			sentences.add(new SentenceSequences(currSentence, sequence));

			sentence.poll();
			sentence.add(sequence);
		}

		return sentences;
	}

	private Stream<String> lineToTokens(String line) {
		String[] words = line.split("( |\\t)+");
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].trim();
		}
		words[words.length - 1] = words[words.length - 1] + EOL;

		return Stream.of(words);
	}

	private Stream<SentenceSequences> generateSentenceSequences(TextFile textFile, int coherence) {
		List<String> tokens = Stream.of(textFile.getText().split(EOL)) //
				.map(String::trim) //
				.flatMap(this::lineToTokens) //
				.collect(Collectors.toList());

		return this.parseText(tokens, coherence).stream();
	}

	/*
	 * Getting Setence Sequences - END
	 */

	/*
	 * Organizing Setence Sequences - BEGIN
	 */

	private List<SentenceSequences> getSetenceSequencesComplete(List<SentenceSequences> sentenceSequences) {
		List<SentenceSequences> sentenceSequencesComplete = new ArrayList<>();
		for (SentenceSequences sentenceSequences2 : sentenceSequences) {
			int index = sentenceSequencesComplete.indexOf(sentenceSequences2);
			if (index >= 0) {
				sentenceSequencesComplete.get(index).addSequence(sentenceSequences2.getSequences().get(0));
			} else {
				sentenceSequencesComplete.add(sentenceSequences2);
			}
		}
		return sentenceSequencesComplete;
	}

	/*
	 * Organizing Setence Sequences - END
	 */

	public List<TextFile> process(List<String> filesPath, List<String> directoriesPaths, StyleParameters parameters) {
		List<TextFile> textFiles = getTextFiles(filesPath, directoriesPaths);

		List<SentenceSequences> sentenceSequences = textFiles.stream() //
				.flatMap(tf -> generateSentenceSequences(tf, parameters.getCoherence())) //
				.sorted() //
				.collect(Collectors.toList());

		List<SentenceSequences> sentenceSequencesComplete = getSetenceSequencesComplete(sentenceSequences);

		sentenceSequencesComplete.forEach(ss -> logger.trace(ss.toString()));

		return null;
	}

}
