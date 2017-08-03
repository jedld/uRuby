package org.mruby.parser;

public class ConstantStringDefinition extends ConstantLiteral {
	public String toString() {
		return "'" + super.toString() + "'";
	}
}
