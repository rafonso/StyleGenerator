package stylegenerator.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StyleParameters {

	/* Attributes - BEGIN */

	private boolean waitForEndOfText;

	private Integer quantityOfWords;

	private boolean waitForEndOfPrhase;

	private Integer quantityOfPhrases;

	private Integer quantityOfLines;

	/* Attributes - END */

}
