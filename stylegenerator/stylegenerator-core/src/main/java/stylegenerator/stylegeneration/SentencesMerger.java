package stylegenerator.stylegeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import stylegenerator.core.Sentence;
import stylegenerator.core.Word;
import stylegenerator.core.WordsListComparator;

public class SentencesMerger implements Collector<Sentence, Map<List<Word>, Sentence>, List<Sentence>> {

	private void merge(Map<List<Word>, Sentence> mergedSentences, Sentence currentSentence) {
		Sentence storedSentence = mergedSentences.get(currentSentence.getWords());
		if (storedSentence == null) {
			mergedSentences.put(currentSentence.getWords(), currentSentence);
		} else {
			storedSentence.addSequences(currentSentence.getSequences());
		}
	}

	private Map<List<Word>, Sentence> combineMergedSentences(Map<List<Word>, Sentence> map1,
			Map<List<Word>, Sentence> map2) {
		map1.putAll(map2);
		return map1;
	}

	@Override
	public BiConsumer<Map<List<Word>, Sentence>, Sentence> accumulator() {
		return this::merge;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.emptySet();
	}

	@Override
	public BinaryOperator<Map<List<Word>, Sentence>> combiner() {
		return this::combineMergedSentences;
	}

	@Override
	public Function<Map<List<Word>, Sentence>, List<Sentence>> finisher() {
		return (m) -> new ArrayList<>(m.values());
	}

	@Override
	public Supplier<Map<List<Word>, Sentence>> supplier() {
		return () -> new TreeMap<>(new WordsListComparator());
	}

}