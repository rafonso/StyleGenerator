package stylegenerator.textgeneration;

import stylegenerator.core.Word;

public class EndOfTextTerminator implements TextTerminator {

	@Override
	public boolean endText(Word word) {
		return word.isEot();
	}

}
