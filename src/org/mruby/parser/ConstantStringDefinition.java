package org.mruby.parser;

public class ConstantStringDefinition extends ConstantLiteral {
	StringBuffer valueBuffer = new StringBuffer();
	
	public void appendValue(char currentChar) {
		// TODO Auto-generated method stub
		valueBuffer.append(currentChar);
	}

	public String value() {
		return valueBuffer.toString();
	}
	
	public String toString() {
		return valueBuffer.toString();
	}
}
