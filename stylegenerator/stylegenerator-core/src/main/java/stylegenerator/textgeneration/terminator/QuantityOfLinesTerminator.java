package stylegenerator.textgeneration.terminator;

import stylegenerator.core.Word;

class QuantityOfLinesTerminator extends QuantityTerminator {

	public QuantityOfLinesTerminator(int quantity) {
		super(quantity, Word::isEol);
	}

}
