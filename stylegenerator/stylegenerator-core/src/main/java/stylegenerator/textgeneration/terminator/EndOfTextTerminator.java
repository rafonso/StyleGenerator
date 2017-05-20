package stylegenerator.textgeneration.terminator;

import stylegenerator.core.Word;

class EndOfTextTerminator implements TextTerminator {

	@Override
	public boolean endText(Word word) {
		return word.isEot();
	}

}
