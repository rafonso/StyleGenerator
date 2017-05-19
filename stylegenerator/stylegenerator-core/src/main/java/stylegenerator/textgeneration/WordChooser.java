package stylegenerator.textgeneration;

import java.util.List;
import java.util.function.Function;

import stylegenerator.core.Constants;
import stylegenerator.core.Sentence;
import stylegenerator.core.Word;

class WordChooser implements Function<Sentence, Word> {

	private WordChooser() {
		// TODO Auto-generated constructor stub
	}

	public static WordChooser INSTANCE = new WordChooser();

	@Override
	public Word apply(Sentence sentence) {
		if(sentence.getSequences().isEmpty()) {
			throw new IllegalStateException("Sentence with no Sequences: " + sentence);
		}
		if (sentence.getSequences().size() == 1) {
			return sentence.getSequences().get(0);
		}

		int position = Constants.RANDOM.nextInt(sentence.getSequences().size());

		return sentence.getSequences().get(position);
	}

}
