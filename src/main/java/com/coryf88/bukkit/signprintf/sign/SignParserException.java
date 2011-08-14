package com.coryf88.bukkit.signprintf.sign;

@SuppressWarnings("serial")
public class SignParserException extends Exception {
	private final String error;
	private final String line;
	private final int position;

	public SignParserException(String error, String line, int position) {
		super(error);
		this.error = error;
		this.line = line;
		this.position = position;
	}

	/**
	 * Get the parser error.
	 * 
	 * @return The error.
	 */
	public String getError() {
		return this.error;
	}

	/**
	 * Get the line that the error occured on.
	 * 
	 * @return The line.
	 */
	public String getLine() {
		return this.line;
	}

	/**
	 * Get the error position.
	 * 
	 * @return The position.
	 */
	public int getPosition() {
		return this.position;
	}
}
