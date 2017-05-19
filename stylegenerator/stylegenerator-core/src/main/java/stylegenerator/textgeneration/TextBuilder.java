package stylegenerator.textgeneration;

import stylegenerator.core.Constants;
import stylegenerator.core.Word;

class TextBuilder {

	private StringBuilder text = new StringBuilder();

	TextBuilder append(Word word) {
		if (text.length() == 0) {
			// No Appending
		} else if (word.isBol()) {
			text.append(Constants.EOL);
		} else {
			text.append(" ");
		}
		text.append(word.getValue());

		return this;
	}

	public String build() {
		return text.toString();
	}

}
