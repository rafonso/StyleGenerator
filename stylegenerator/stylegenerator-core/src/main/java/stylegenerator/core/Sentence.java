package stylegenerator.core;

import java.util.ArrayList;
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
		this.words = words;
	}

	/* Constructors - END */

	/* Getters & Setters - BEGIN */

	@JsonIgnore
	public boolean isBot() {
		return this.words.get(0).isBot();
	}

	@JsonIgnore
	public boolean isBol() {
		return this.words.get(0).isBol();
	}

	@JsonIgnore
	public boolean isBop() {
		return this.words.get(0).isBop();
	}

	/* Getters & Setters - END */

	/* HELPER METHODS - BEGIN */

	public void addSequence(Word sequence) {
		this.sequences.add(sequence);
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
