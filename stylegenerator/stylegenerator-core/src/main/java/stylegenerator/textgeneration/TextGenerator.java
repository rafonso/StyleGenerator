package stylegenerator.textgeneration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import stylegenerator.core.Constants;
import stylegenerator.core.Sentence;
import stylegenerator.core.StyleParameters;
import stylegenerator.core.Word;
import stylegenerator.textgeneration.firstsentencechooser.FirstSentenceChooserFactory;
import stylegenerator.textgeneration.terminator.TextTerminator;
import stylegenerator.textgeneration.terminator.TextTerminatorFactory;

@Slf4j
public class TextGenerator {

	private StyleParameters parameters;

	public TextGenerator(StyleParameters parameters) {
		this.parameters = parameters;
	}

	private Sentence getInitialSentence(List<Sentence> sentences) {
		List<Sentence> initialsSentences = sentences.stream() //
				.filter(FirstSentenceChooserFactory.getChooser(parameters)) //
				.collect(Collectors.toList());
		// log.debug("Initial Sentences: {}", initialsSentences);

		int initialIndex = Constants.RANDOM.nextInt(initialsSentences.size());
		// log.debug("index: {}", initialIndex);
		return initialsSentences.get(initialIndex);
	}

	public String generateText(List<Sentence> sentences) {

		Sentence initialSentence = getInitialSentence(sentences);

		TextBuilder builder = new TextBuilder();
		for (Word word : initialSentence.getWords()) {
			builder.append(word);
		}

		Queue<Word> words = new LinkedList<>(initialSentence.getWords());
		Word nextWord = WordChooser.INSTANCE.apply(initialSentence);
		builder.append(nextWord);
		TextTerminator terminator = TextTerminatorFactory.getTerminator(parameters);

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
