package org.mruby.parser;

import java.util.ArrayList;

public class MethodDefinition {
	StringBuilder name = new StringBuilder();
	ArrayList<ParameterDefinition> parameters = new ArrayList<>();
	
	public void appendToName(char currentChar) {
		name.append(currentChar);
	}

	public void addParameter(ParameterDefinition parameterDefinition) {
		parameters.add(parameterDefinition);
	}
}
