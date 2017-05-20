package stylegenerator.textgeneration.terminator;

import stylegenerator.core.Word;

class QuantityOfWordsTerminator implements TextTerminator {

	private final int quantity;

	private int currentWordPosition;

	public QuantityOfWordsTerminator(int quantity) {
		this.quantity = quantity;
		currentWordPosition = 0;
	}

	@Override
	public boolean endText(Word word) {
		currentWordPosition++;

		return currentWordPosition >= quantity;
	}

}
