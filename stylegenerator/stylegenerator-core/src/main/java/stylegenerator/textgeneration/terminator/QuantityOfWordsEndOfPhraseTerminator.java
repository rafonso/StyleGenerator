package stylegenerator.textgeneration.terminator;

import stylegenerator.core.Word;

class QuantityOfWordsEndOfPhraseTerminator extends QuantityOfWordsTerminator {

	public QuantityOfWordsEndOfPhraseTerminator(int quantity) {
		super(quantity);
	}

	@Override
	public boolean endText(Word word) {
		return super.endText(word) && word.isEop();
	}

}
