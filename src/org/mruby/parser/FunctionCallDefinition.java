package org.mruby.parser;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallDefinition {
	String name;
	ArrayList<FunctionCallParam> params = new ArrayList<>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void addParam(FunctionCallParam functionCallParam) {
		params.add(functionCallParam);
	}

	public List<FunctionCallParam> getParams() {
		return params;
	}
}
