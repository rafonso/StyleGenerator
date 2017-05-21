package stylegenerator.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Sentence implements Comparable<Sentence> {

	/* Attributes - BEGIN */

	private List<Word> words;

	private List<Word> sequences = new ArrayList<>();

	/* Attributes - END */

	/* Constructors - BEGIN */

	public Sentence(List<Word> words) {
		this.words = Collections.unmodifiableList(words);
	}

	/* Constructors - END */

	private Word getFirstWord() {
		return this.words.get(0);
	}

	/* Getters & Setters - BEGIN */
	
	@JsonIgnore
	public boolean isBot() {
		return getFirstWord().isBot();
	}

	@JsonIgnore
	public boolean isBol() {
		return getFirstWord().isBol();
	}

	@JsonIgnore
	public boolean isBop() {
		return getFirstWord().isBop();
	}

	/* Getters & Setters - END */

	/* HELPER METHODS - BEGIN */

	public void addSequence(Word sequence) {
		this.sequences.add(sequence);
	}

	public void addSequences(List<Word> sequences) {
		this.sequences.addAll(sequences);
	}

	public int compareTo(Sentence other) {
		return new WordsListComparator().compare(this.words, other.words);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (words == null) {
			if (other.words != null)
				return false;
		} else if (!words.equals(other.words))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((words == null) ? 0 : words.hashCode());
		return result;
	}

	/* HELPER METHODS - END */

}
