package stylegenerator.core;

public class StyleParameters {

	/* Attributes - BEGIN */

	private int coherence;

	/* Attributes - END */

	/* Getters & Setters - BEGIN */

	public int getCoherence() {
		return coherence;
	}

	public void setCoherence(int coherence) {
		this.coherence = coherence;
	}

	/* Getters & Setters - END */

	@Override
	public String toString() {
		return "StyleParameters [coherence=" + coherence + "]";
	}

}
