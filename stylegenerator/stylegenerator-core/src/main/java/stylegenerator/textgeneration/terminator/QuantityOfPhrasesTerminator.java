package stylegenerator.textgeneration.terminator;

import stylegenerator.core.Word;

class QuantityOfPhrasesTerminator extends QuantityTerminator {

	public QuantityOfPhrasesTerminator(int quantity) {
		super(quantity, Word::isEop);
	}

}
