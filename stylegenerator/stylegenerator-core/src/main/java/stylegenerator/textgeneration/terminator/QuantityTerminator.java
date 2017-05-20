package stylegenerator.textgeneration.terminator;

import java.util.function.Predicate;

import stylegenerator.core.Word;

abstract class QuantityTerminator implements TextTerminator {

	private final int finalQuantity;

	private final Predicate<Word> criteria;

	private int currentQuantity;

	protected QuantityTerminator(int finalQuantity, Predicate<Word> criteria) {
		this.finalQuantity = finalQuantity;
		this.criteria = criteria;
		this.currentQuantity = 0;
	}

	@Override
	public boolean endText(Word word) {
		if (this.criteria.test(word)) {
			this.currentQuantity++;
		}
		return (this.currentQuantity >= this.finalQuantity);
	}

}
