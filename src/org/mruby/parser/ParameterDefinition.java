package org.mruby.parser;

public class ParameterDefinition {
	StringBuilder name = new StringBuilder();
	private ConstantLiteral defaultValue;
	
	public ConstantLiteral getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(ConstantLiteral setDefaultValue) {
		this.defaultValue = setDefaultValue;
	}

	public void appendToName(char currentChar) {
		name.append(currentChar);
	}
}
