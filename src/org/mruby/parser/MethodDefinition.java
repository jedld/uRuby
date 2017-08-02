package org.mruby.parser;

import java.util.ArrayList;

public class MethodDefinition {
	public static final int CLASS_SECTION_PUBLIC = 0;
	public static final int CLASS_SECTION_PROTECTED = 1;
	public static final int CLASS_SECTION_PRIVATE = 2;
	
	StringBuilder name = new StringBuilder();
	ArrayList<ParameterDefinition> parameters = new ArrayList<>();
	private int section;
	
	public MethodDefinition(int section) {
		this.section = section;
	}
	
	public int getSection() {
		return section;
	}
	
	public void appendToName(char currentChar) {
		name.append(currentChar);
	}

	public void addParameter(ParameterDefinition parameterDefinition) {
		parameters.add(parameterDefinition);
	}
}
