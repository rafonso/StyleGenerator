package stylegenerator.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(exclude = "sequences")
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
		if (this.words.size() != other.words.size()) {
			throw new IllegalStateException();
		}

		for (int i = 0; i < this.words.size(); i++) {
			int diff = this.words.get(i).compareTo(other.words.get(i));
			if (diff != 0) {
				return diff;
			}
		}

		return 0;
	}

	/* HELPER METHODS - END */

}
