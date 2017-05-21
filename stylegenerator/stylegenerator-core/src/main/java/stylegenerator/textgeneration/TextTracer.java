package stylegenerator.textgeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;
import stylegenerator.core.Sentence;
import stylegenerator.core.Word;

@Data
public class TextTracer {

	private final List<Word> initialWords;

	private Sentence lastSentence;
	
	private Collection<Word> lastWords;

	private final List<Integer> sequences = new ArrayList<Integer>();

	protected TextTracer(Sentence initialSentence) {
		this.initialWords = initialSentence.getWords();
		this.lastWords = this.initialWords;
		this.lastSentence = initialSentence;
	}

	public void addSequence(int sequence) {
		sequences.add(sequence);
	}

}
