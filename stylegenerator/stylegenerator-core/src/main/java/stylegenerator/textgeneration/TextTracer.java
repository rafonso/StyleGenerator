package stylegenerator.textgeneration;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import stylegenerator.core.Word;

@Data
public class TextTracer {

	private final List<Word> initialWords;

	private Word lastWord;

	private final List<Integer> sequences = new ArrayList<Integer>();

	protected TextTracer(List<Word> initialWords) {
		this.initialWords = initialWords;
		this.lastWord = initialWords.get(initialWords.size() - 1);
	}

	public void addSequence(int sequence) {
		sequences.add(sequence);
	}

}
