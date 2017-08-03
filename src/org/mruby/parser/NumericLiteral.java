package org.mruby.parser;

public class NumericLiteral extends ConstantLiteral {
	public NumericLiteral(String value) {
		valueBuffer.append(value);
	}
}
