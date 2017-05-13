package stylegenerator.core;

public class Sequence implements Comparable<Sequence> {

	/* Attributes - BEGIN */

	private String value;

	private SequencePosition position;

	/* Attributes - END */

	/* Constructors - BEGIN */

	public Sequence(String value, SequencePosition position) {
		this.value = value;
		this.position = position;
	}

	public Sequence() {
	}

	/* Constructors - END */

	/* Getters & Setters - BEGIN */

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SequencePosition getPosition() {
		return position;
	}

	public void setPosition(SequencePosition position) {
		this.position = position;
	}

	/* Getters & Setters - END */

	/* HELPER METHODS - BEGIN */

	@Override
	public String toString() {
		String strPosition = (position == SequencePosition.NONE) ? "" : (" (" + position + ")");
		return "'" + value + "'" + strPosition;
	}

	public int compareTo(Sequence other) {
		int result = this.getValue().compareTo(other.getValue());

		return (result != 0) ? result : this.getPosition().compareTo(other.getPosition());
	}

	/* HELPER METHODS - END */

}
