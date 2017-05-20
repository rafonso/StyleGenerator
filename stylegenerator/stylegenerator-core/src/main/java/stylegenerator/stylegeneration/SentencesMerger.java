package stylegenerator.stylegeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import stylegenerator.core.Sentence;

public class SentencesMerger implements Collector<List<Sentence>, List<Sentence>, List<Sentence>> {

	private void merge(List<Sentence> mergedSentences, List<Sentence> currentSentences) {
		for (Sentence sentence : currentSentences) {
			int index = mergedSentences.indexOf(sentence);
			if (index >= 0) {
				mergedSentences.get(index).addSequences(sentence.getSequences());
			} else {
				mergedSentences.add(sentence);
			}
		}
	}
	
	private List<Sentence> sortAndReturn(List<Sentence> sentences) {
		Collections.sort(sentences);
		return sentences;
	}

	@Override
	public BiConsumer<List<Sentence>, List<Sentence>> accumulator() {
		return this::merge;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.singleton(Characteristics.IDENTITY_FINISH);
	}

	@Override
	public BinaryOperator<List<Sentence>> combiner() {
		return (a, b) -> a;
	}

	@Override
	public Function<List<Sentence>, List<Sentence>> finisher() {
		return this::sortAndReturn;
	}

	@Override
	public Supplier<List<Sentence>> supplier() {
		return () -> new ArrayList<Sentence>();
	}

}