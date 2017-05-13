package stylegenerator.core;

import java.util.ArrayList;
import java.util.List;

public class Sentence implements Comparable<Sentence> {

	/* Attributes - BEGIN */

	private String value;

	private SentencePosition position;

	private List<Sequence> sequences = new ArrayList<Sequence>();

	/* Attributes - END */

	/* Constructors - BEGIN */

	public Sentence(String sentence, SentencePosition position, Sequence firstSequence) {
		this.value = sentence;
		this.position = position;
		this.sequences.add(firstSequence);
	}

	public Sentence() {
	}

	/* Constructors - END */

	/* Getters & Setters - BEGIN */

	public String getValue() {
		return value;
	}

	public void setValue(String sentence) {
		this.value = sentence;
	}

	public SentencePosition getPosition() {
		return position;
	}

	public void setPosition(SentencePosition position) {
		this.position = position;
	}

	public List<Sequence> getSequences() {
		return sequences;
	}

	public void setSequences(List<Sequence> sequences) {
		this.sequences = sequences;
	}

	/* Getters & Setters - END */

	/* HELPER METHODS - BEGIN */

	public void addSequence(Sequence sequence) {
		this.sequences.add(sequence);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Sentence other = (Sentence) obj;
		if (position != other.position) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String strPosition = (position == SentencePosition.NONE) ? "" : ("(" + position + ") ");

		return "('" + value + "' " + strPosition + " -> " + sequences;
	}

	public int compareTo(Sentence other) {
		int result = this.getValue().compareTo(other.getValue());

		return (result != 0) ? result : this.getPosition().compareTo(other.getPosition());
	}

	/* HELPER METHODS - END */

}
