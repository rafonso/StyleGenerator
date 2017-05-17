package stylegenerator.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextFile {

	private static final int CHUNK_SIZE = 50;

	private final String fileName;

	@JsonIgnore
	private final String text;

	public TextFile(String fileName, String text) {
		this.fileName = fileName;
		this.text = text;
	}

	public String getFileName() {
		return fileName;
	}

	public String getText() {
		return text;
	}

	@JsonProperty("size")
	public int getFileSize() {
		return text.length();
	}

	@Override
	public String toString() {
		String chunk = (text.length() < CHUNK_SIZE) ? text : text.substring(0, CHUNK_SIZE) + "...";

		return String.format("[%s (%d bytes): '%s']", this.fileName, this.getFileSize(), chunk);
	}

}
