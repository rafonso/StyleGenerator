package stylegenerator.textgeneration.terminator;

import stylegenerator.core.Word;

public class QuantityOfPhrasesTerminator implements TextTerminator {

	private final int quantity;

	private int currentPrhasesPosition;

	public QuantityOfPhrasesTerminator(int quantity) {
		this.quantity = quantity;
		this.currentPrhasesPosition = 0;
	}

	@Override
	public boolean endText(Word word) {
		if (word.isEop()) {
			this.currentPrhasesPosition++;
		}
		return (this.currentPrhasesPosition >= this.quantity);
	}

}
