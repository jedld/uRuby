package org.mruby.parser;

public class FunctionCallParam {
	Object value;
	
	public FunctionCallParam(ConstantStringDefinition constantString) {
		this.value = constantString;
	}
	
	public String toString() {
		if (value instanceof ConstantStringDefinition) {
			return ((ConstantStringDefinition)value).toString();
		}
		return value.toString();
	}
}
