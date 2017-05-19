package stylegenerator.textgeneration;

import stylegenerator.core.Word;

@FunctionalInterface
interface TextTerminator {

	public boolean endText(Word word);

}
