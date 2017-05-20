package stylegenerator.textgeneration;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import stylegenerator.core.Constants;
import stylegenerator.core.Sentence;
import stylegenerator.core.Word;
import stylegenerator.textgeneration.firstsentencechooser.FirstSentenceChooserFactory;
import stylegenerator.textgeneration.terminator.TextTerminator;
import stylegenerator.textgeneration.terminator.TextTerminatorFactory;

@Slf4j
public class TextGenerator {

	private final TextParameter parameter;

	public TextGenerator(TextParameter parameter) {
		this.parameter = parameter;
	}

	private Sentence getInitialSentence(List<Sentence> sentences) {
		List<Sentence> initialsSentences = sentences.stream() //
				.filter(FirstSentenceChooserFactory.getChooser(parameter)) //
				.collect(Collectors.toList());

		int initialIndex = Constants.RANDOM.nextInt(initialsSentences.size());
		return initialsSentences.get(initialIndex);
	}

	private Word getNextWord(Sentence sentence, TextTracer tracer) {
		int nextPosition = (sentence.getSequences().size() == 1) ? //
				0 : //
				Constants.RANDOM.nextInt(sentence.getSequences().size());
		Word nextWord = sentence.getSequences().get(nextPosition);

		tracer.addSequence(nextPosition);
		tracer.setLastWord(nextWord);

		return nextWord;
	}

	public String generateText(List<Sentence> sentences) {

		Sentence initialSentence = getInitialSentence(sentences);

		TextBuilder builder = initialSentence.getWords().stream() //
				.collect(TextBuilder::new, (tb, word) -> tb.append(word), (a, b) -> {
				});

		Queue<Word> words = new LinkedList<>(initialSentence.getWords());
		TextTracer tracer = new TextTracer(initialSentence.getWords());

		Word nextWord = getNextWord(initialSentence, tracer);
		builder.append(nextWord);

		TextTerminator terminator = TextTerminatorFactory.getTerminator(parameter);
		while (!terminator.endText(nextWord)) {
			words.poll();
			words.add(nextWord);

			Sentence sentence = sentences.stream() //
					.filter((s) -> s.getWords().equals(words)) //
					.findFirst() //
					.get();

			nextWord = getNextWord(sentence, tracer);
			builder.append(nextWord);
		}

		log.debug(tracer.toString());

		return builder.build();
	}

}
