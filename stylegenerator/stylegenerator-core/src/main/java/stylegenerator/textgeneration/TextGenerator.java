package stylegenerator.textgeneration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import stylegenerator.core.Constants;
import stylegenerator.core.Sentence;
import stylegenerator.core.StyleParameters;
import stylegenerator.core.Word;

@Slf4j
public class TextGenerator {

	private Predicate<Sentence> getStarterFilter(StyleParameters parameters) {
		if (parameters.isWaitForEndOfText()) {
			return Sentence::isBot;
		}
		if ((parameters.getQuantityOfWords() != null) //
				|| (parameters.getQuantityOfPhrases() != null) //
				|| (parameters.getQuantityOfLines() != null)) {
			return Sentence::isBol;
		}

		throw new IllegalStateException("No Begin of text filter defined. Parameters: " + parameters);
	}

	private TextTerminator getTerminator(StyleParameters parameters) {
		if (parameters.isWaitForEndOfText()) {
			return new EndOfTextTerminator();
		}
		if (parameters.getQuantityOfWords() != null) {
			if (parameters.isWaitForEndOfPrhase()) {
				return new QuantityOfWordsEndOfPhraseTerminator(parameters.getQuantityOfWords());
			}
			return new QuantityOfWordsTerminator(parameters.getQuantityOfWords());
		}
		if (parameters.getQuantityOfPhrases() != null) {
			return new QuantityOfPhrasesTerminator(parameters.getQuantityOfPhrases());
		}
		if (parameters.getQuantityOfLines() != null) {
			return new QuantityOfLinesTerminator(parameters.getQuantityOfLines());
		}

		throw new IllegalStateException("No text terminator defined. Parameters: " + parameters);
	}

	private Sentence getInitialSentence(List<Sentence> sentences, StyleParameters parameters) {
		List<Sentence> initialsSentences = sentences.stream().filter(getStarterFilter(parameters))
				.collect(Collectors.toList());
		// log.debug("Initial Sentences: {}", initialsSentences);

		int initialIndex = Constants.RANDOM.nextInt(initialsSentences.size());
		// log.debug("index: {}", initialIndex);
		Sentence initialSentence = initialsSentences.get(initialIndex);
		// log.debug("Initial Sentence: {}", initialSentence);
		return initialSentence;
	}

	public String generateText(List<Sentence> sentences, StyleParameters parameters) {

		Sentence initialSentence = getInitialSentence(sentences, parameters);

		TextBuilder builder = new TextBuilder();
		for (Word word : initialSentence.getWords()) {
			builder.append(word);
		}

		Queue<Word> words = new LinkedList<>(initialSentence.getWords());
		Word nextWord = WordChooser.INSTANCE.apply(initialSentence);
		builder.append(nextWord);
		TextTerminator terminator = getTerminator(parameters);

		while (!terminator.endText(nextWord)) {
			words.poll();
			words.add(nextWord);
			Sentence sentence = sentences.stream() //
					.filter((sentence1) -> sentence1.getWords().equals(new ArrayList<>(words))) //
					.findFirst() //
					.get();
			// log.trace("Sentence: {}", sentence);
			nextWord = WordChooser.INSTANCE.apply(sentence);
			// log.trace("nextWord: {}", nextWord);
			builder.append(nextWord);
			// log.trace("");
		}

		return builder.build();
	}

}
