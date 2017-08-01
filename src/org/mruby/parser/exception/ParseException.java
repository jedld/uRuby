package org.mruby.parser.exception;

public class ParseException extends RuntimeException{
	int col, line;
	private String message;
	
	public ParseException(int col, int line, String message) {
		this.col = col;
		this.line = line;
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return "Syntax Error: line " + line + ", col " + col + ": " + message;
	}
}
