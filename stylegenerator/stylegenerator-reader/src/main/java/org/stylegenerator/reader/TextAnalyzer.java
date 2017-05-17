package org.stylegenerator.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stylegenerator.core.Sentence;
import stylegenerator.core.TextFile;
import stylegenerator.core.Word;

public class TextAnalyzer {

	static Logger logger = LoggerFactory.getLogger(TextAnalyzer.class);

	private static final String EOL = System.getProperty("line.separator");

	

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

	private List<Sentence> parseText(List<Word> words, int coherence) {
		Queue<Word> wordQueue = new LinkedList<>();
		Iterator<Word> itWords = words.iterator();
		while (wordQueue.size() < coherence) {
			wordQueue.add(itWords.next());
		}

		List<Sentence> sentences = new ArrayList<>();
		while (itWords.hasNext()) {
			Word sequence = itWords.next();
			Sentence sentence = new Sentence(new ArrayList<>(wordQueue));
			sentence.addSequence(sequence);

			sentences.add(sentence);

			wordQueue.poll();
			wordQueue.add(sequence);
		}

		return sentences;
	}

	private Word tokenToWord(String token, Word priorWord, boolean eot) {
		boolean eol = false;
		if (token.endsWith(EOL)) {
			token = token.replace(EOL, "");
			eol = true;
		}

		Word word = new Word(token);
		word.setBot(priorWord == null);
		word.setBol(word.isBot() || priorWord.isEol());
		word.setBop(word.isBol() || priorWord.isEop());
		word.setEot(eot);
		word.setEol(word.isEot() || eol);
		word.setEop(token.endsWith(".") || token.endsWith("?") || token.endsWith("!"));

		return word;
	}

	private List<Word> tokensToWords(List<String> tokens) {
		List<Word> words = new ArrayList<>(tokens.size());
		Word priorWord = null;

		for (Iterator<String> itToken = tokens.iterator(); itToken.hasNext();) {
			Word word = tokenToWord(itToken.next(), priorWord, !itToken.hasNext());

			words.add(word);
			priorWord = word;
		}

		return words;
	}

	private Stream<String> lineToTokens(String line) {
		String[] words = line.split("( |\\t)+");
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].trim();
		}
		words[words.length - 1] = words[words.length - 1] + EOL;

		return Stream.of(words);
	}

	private Stream<Sentence> generateFileSentences(TextFile textFile, int coherence) {
		List<String> tokens = Stream.of(textFile.getText().split(EOL)) //
				.map(String::trim) //
				.flatMap(this::lineToTokens) //
				.collect(Collectors.toList());

//		logger.debug(tokens.stream().collect(Collectors.joining("', '", "'", "'")));

		List<Word> words = tokensToWords(tokens);

//		logger.debug(words.toString());

		 List<Sentence> sentences = this.parseText(words, coherence);
//		 sentences.forEach(s -> logger.debug(s.toString()));
		 
		 
		return sentences.stream();
	}

	/*
	 * Getting Setence Sequences - END
	 */

	/*
	 * Organizing Setence Sequences - BEGIN
	 */

	private List<Sentence> getSetenceSequencesComplete(List<Sentence> sentenceSequences) {
		List<Sentence> sentenceSequencesComplete = new ArrayList<>();
		for (Sentence sentenceSequences2 : sentenceSequences) {
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

	public List<Sentence> process(List<String> filesPath, List<String> directoriesPaths, int coherence) {
		List<TextFile> textFiles = getTextFiles(filesPath, directoriesPaths);

		List<Sentence> sentenceSequences = textFiles.stream() //
				.flatMap(tf -> generateFileSentences(tf, coherence)) //
				.sorted() //
				.collect(Collectors.toList());

		List<Sentence> sentenceSequencesComplete = getSetenceSequencesComplete(sentenceSequences);
		
		sentenceSequencesComplete.stream().filter(s -> s.getSequences().size() > 1).forEach(System.out::println);

		// sentenceSequencesComplete.forEach(ss -> logger.trace(ss.toString()));

		return sentenceSequencesComplete;
	}

}
