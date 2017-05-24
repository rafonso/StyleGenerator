package stylegenerator.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import stylegenerator.core.Sentence;
import stylegenerator.core.Word;

@Slf4j
public class SentenceEvaluator implements Function<Sentence, Stream<InvalidSentence>> {

	private final List<Sentence> styleSentences;

	public SentenceEvaluator(List<Sentence> styleSentences) {
		this.styleSentences = styleSentences;
	}

	private List<Word> getSearchedWords(List<Word> finalWords, Word sequence) {
		List<Word> searchedWords = new ArrayList<>(finalWords);
		searchedWords.add(sequence);

		return searchedWords;
	}

	private boolean wordsArePresent(List<Word> searchedWords) {
		return this.styleSentences.stream().filter(s -> s.sameWords(searchedWords)).findFirst().isPresent();
	}

	@Override
	public Stream<InvalidSentence> apply(Sentence evaluatedSentence) {
		List<Word> finalWords = evaluatedSentence.getWords().subList(1, evaluatedSentence.getWords().size());
		if (log.isTraceEnabled()) {
			log.trace("Evaluating {}", evaluatedSentence.getWords());
		}

		return evaluatedSentence.getSequences().stream() //
				.map(w -> getSearchedWords(finalWords, w)) //
				.filter(((Predicate<? super List<Word>>) this::wordsArePresent).negate()) //
				.map(absentWords -> new InvalidSentence(absentWords, evaluatedSentence));
	}

}
