package stylegenerator.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Constants {

	public static final String DEFAULT_FILE_NAME_PATTERN = "yyyy-MM-dd-HH-MM-ss";
	
	public static final String FILE_STYLE_EXTENSION = ".style.json";

	public static final String EOL = System.getProperty("line.separator");

	public static final Charset CHARSET = StandardCharsets.UTF_8;
	
	public static final Random RANDOM = new Random();

}
