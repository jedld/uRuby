package org.mruby.parser;

import java.util.ArrayList;

public class ClassDefinition {
	StringBuilder name = new StringBuilder();
	StringBuilder extendName = new StringBuilder();
	ArrayList<MethodDefinition> methods = new ArrayList<>();
	
	public ClassDefinition() {
		
	}
	
	public void appendToName(char currentChar) {
		name.append(currentChar);
	}

	public void appendToExtend(char currentChar) {
		extendName.append(currentChar);
	}

	public void addMethod(MethodDefinition methodDefinition) {
		methods.add(methodDefinition);
	}
}
