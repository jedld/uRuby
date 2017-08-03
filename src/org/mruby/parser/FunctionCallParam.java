package org.mruby.parser;

public class FunctionCallParam {
	Object value;
	
	public FunctionCallParam(ConstantLiteral constantString) {
		this.value = constantString;
	}
	
	public String toString() {
		if (value instanceof ConstantStringDefinition) {
			return ((ConstantStringDefinition)value).toString();
		}
		if (value instanceof InterpolatedStringDefinition) {
			return "\"" + ((InterpolatedStringDefinition)value).toString() + "\"";
		}
		return value.toString();
	}
}
