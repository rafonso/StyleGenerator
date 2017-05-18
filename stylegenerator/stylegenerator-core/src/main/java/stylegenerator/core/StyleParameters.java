package stylegenerator.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StyleParameters {

	/* Attributes - BEGIN */

	private Integer quantityOfWords;
	
	private boolean waitForPrhaseEnd;

	/* Attributes - END */

}
