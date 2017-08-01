package org.mruby.parser;

public class ParameterDefinition {
	StringBuilder name = new StringBuilder();
	
	public void appendToName(char currentChar) {
		name.append(currentChar);
	}
}
