package stylegenerator.textgeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Data;
import stylegenerator.core.Sentence;
import stylegenerator.core.Word;

@Data
public class TextInfo {

	private final List<Word> initialWords;

	private Sentence lastSentence;

	private Collection<Word> lastWords;

	private int sentencesQuantity;

	private int totalRandomness;
	
	private double textRandomness;

	private final List<Integer> sequences = new ArrayList<Integer>();

	protected TextInfo(Sentence initialSentence) {
		this.initialWords = initialSentence.getWords();
		this.lastWords = this.initialWords;
		this.lastSentence = initialSentence;
		this.sentencesQuantity = 1;
		this.totalRandomness = initialSentence.getRandomness();
	}

	public void addSequence(int sequence) {
		sequences.add(sequence);
		this.sentencesQuantity++;
		this.textRandomness = (double) this.totalRandomness /this.sentencesQuantity;
	}

	public void addRandomness(int randomness) {
		this.totalRandomness += randomness;
		this.textRandomness = (double) this.totalRandomness /this.sentencesQuantity;
	}

}
