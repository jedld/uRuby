package org.mruby.parser;

public class FunctionCallParam {
	Object value;
	
	public FunctionCallParam(ConstantLiteral constantString) {
		this.value = constantString;
	}
	
	public String toString() {
		return value.toString();
	}
}
