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

	private Predicate<Sentence> getStarterFilter(StyleParameters styleParameters) {
		if (styleParameters.isWaitForEndOfText()) {
			return Sentence::isBot;
		}
		if (styleParameters.getQuantityOfWords() != null) { // qtyOfPhrases,
															// qtyOfLines
			return Sentence::isBol;
		}

		throw new IllegalStateException("No Begin of text filter defined. Parameters: " + styleParameters);
	}

	private TextTerminator getTerminator(StyleParameters styleParameters) {
		if (styleParameters.isWaitForEndOfText()) {
			return new EndOfTextTerminator();
		}
		if (styleParameters.getQuantityOfWords() != null) {
			if (styleParameters.isWaitForEndOfPrhase()) {
				return new QuantityOfWordsEndOfPhraseTerminator(styleParameters.getQuantityOfWords());
			}
			return new QuantityOfWordsTerminator(styleParameters.getQuantityOfWords());
		}

		throw new IllegalStateException("No text terminator defined. Parameters: " + styleParameters);
	}

	private Sentence getInitialSentence(List<Sentence> sentences, StyleParameters styleParameters) {
		List<Sentence> initialsSentences = sentences.stream().filter(getStarterFilter(styleParameters))
				.collect(Collectors.toList());
//		log.debug("Initial Sentences: {}", initialsSentences);

		int initialIndex = Constants.RANDOM.nextInt(initialsSentences.size());
//		log.debug("index: {}", initialIndex);
		Sentence initialSentence = initialsSentences.get(initialIndex);
//		log.debug("Initial Sentence: {}", initialSentence);
		return initialSentence;
	}

	private Predicate<Sentence> getSentenceChooser(Queue<Word> words) {
		List<Word> wordsFilter = new ArrayList<>(words);
		return (sentence) -> sentence.getWords().equals(wordsFilter);
	}

	public String generateText(List<Sentence> sentences, StyleParameters styleParameters) {

		Sentence initialSentence = getInitialSentence(sentences, styleParameters);

		TextBuilder builder = new TextBuilder();
		for (Word word : initialSentence.getWords()) {
			builder.append(word);
		}

		Queue<Word> words = new LinkedList<>(initialSentence.getWords());
		Word nextWord = WordChooser.INSTANCE.apply(initialSentence);
		builder.append(nextWord);
		TextTerminator terminator = getTerminator(styleParameters);

		while (!terminator.endText(nextWord)) {
			words.poll();
			words.add(nextWord);
			Sentence sentence = sentences.stream().filter(this.getSentenceChooser(words)).findFirst().get();
//			log.trace("Sentence: {}", sentence);
			nextWord = WordChooser.INSTANCE.apply(sentence);
//			log.trace("nextWord: {}", nextWord);
			builder.append(nextWord);
//			log.trace("");
		}

		return builder.build();
	}

}
