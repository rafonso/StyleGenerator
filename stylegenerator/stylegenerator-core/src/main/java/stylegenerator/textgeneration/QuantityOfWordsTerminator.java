package stylegenerator.textgeneration;

import stylegenerator.core.Word;

public class QuantityOfWordsTerminator implements TextTerminator {

	private final int quantity;

	private int currentPosition;

	public QuantityOfWordsTerminator(int quantity) {
		this.quantity = quantity;
		currentPosition = 0;
	}

	@Override
	public boolean endText(Word word) {
		currentPosition++;

		return currentPosition >= quantity;
	}

}
