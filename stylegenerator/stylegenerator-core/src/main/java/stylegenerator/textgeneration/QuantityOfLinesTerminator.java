package stylegenerator.textgeneration;

import stylegenerator.core.Word;

public class QuantityOfLinesTerminator implements TextTerminator {

	private final int quantity;

	private int currentLinePosition;

	public QuantityOfLinesTerminator(int quantity) {
		this.quantity = quantity;
		this.currentLinePosition = 0;
	}

	@Override
	public boolean endText(Word word) {
		if (word.isEol()) {
			this.currentLinePosition++;
		}
		return (this.currentLinePosition >= this.quantity);
	}

}
