package stylegenerator.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SentenceSequences implements Comparable<SentenceSequences> {

	/* Attributes - BEGIN */

	private String sentence;

	private List<String> sequences = new ArrayList<String>();

	/* Attributes - END */

	/* Constructors - BEGIN */

	public SentenceSequences(String sentence, String firstSequence) {
		this.sentence = sentence;
		this.sequences.add(firstSequence);
	}

	public SentenceSequences() {
	}

	/* Constructors - END */

	/* Getters & Setters - BEGIN */

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public List<String> getSequences() {
		return sequences;
	}

	public void setSequences(List<String> sequences) {
		this.sequences = sequences;
	}

	/* Getters & Setters - END */

	/* HELPER METHODS - BEGIN */

	public void addSequence(String sequence) {
		this.sequences.add(sequence);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sentence == null) ? 0 : sentence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SentenceSequences other = (SentenceSequences) obj;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if (!sentence.equals(other.sentence))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "('" + sentence + "' -> " + sequences.stream().collect(Collectors.joining("', '", "['", "']")) + ")";
	}

	public int compareTo(SentenceSequences s) {
		return this.getSentence().compareTo(s.getSentence());
	}

	/* HELPER METHODS - END */

}
