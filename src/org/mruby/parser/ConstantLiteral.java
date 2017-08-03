package org.mruby.parser;

public class ConstantLiteral {
	StringBuffer valueBuffer = new StringBuffer();
	
	public void appendValue(char currentChar) {
		// TODO Auto-generated method stub
		valueBuffer.append(currentChar);
	}
	
	public void appendValue(char[] currentChars) {
		// TODO Auto-generated method stub
		valueBuffer.append(currentChars);
	}
	
	public void appendCodePoint(int codepoint) {
		// TODO Auto-generated method stub
		valueBuffer.appendCodePoint(codepoint);
	}

	public String value() {
		return valueBuffer.toString();
	}
	
	public String toString() {
		return valueBuffer.toString();
	}
}
