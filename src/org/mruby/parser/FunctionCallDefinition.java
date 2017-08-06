package org.mruby.parser;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallDefinition extends Expression {
	String name;
	Object object, nextObject, altObject;
	ArrayList<FunctionCallParam> params = new ArrayList<>();
	private boolean isReference;
	
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

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}
	
	public Object getNextObject() {
		return nextObject;
	}
	
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		if (object!=null) {
			strBuffer.append(object.toString() + "." + name);
		} else {
			strBuffer.append(name);
		}
		
		if (isReference) return strBuffer.toString();
		
		strBuffer.append("(");
		for(FunctionCallParam param : getParams()) {
			strBuffer.append(param.toString() + ",");
		}
		strBuffer.append(")");
		return strBuffer.toString();
	}

	public void setNextObject(FunctionCallDefinition functionCallDefinition) {
		this.nextObject = functionCallDefinition;	
	}

	public void setReference(boolean isReference) {
		this.isReference = isReference;
	}

	public void setAltObject(Object altObject) {
		this.altObject = altObject;
	}
}
