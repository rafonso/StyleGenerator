package stylegenerator.textgeneration.terminator;

import stylegenerator.core.Word;

@FunctionalInterface
public interface TextTerminator {

	public boolean endText(Word word);

}
