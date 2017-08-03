package org.mruby.parser;

public class InterpolatedStringDefinition extends ConstantLiteral {
	public String toString() {
		return "\"" + super.toString() + "\"";
	}
}
