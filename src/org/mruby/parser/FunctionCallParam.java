package org.mruby.parser;

import java.util.ArrayList;

public class FunctionCallParam {
	Object value;
	
	public FunctionCallParam(ConstantLiteral constantString) {
		this.value = constantString;
	}
	
	public FunctionCallParam(Object object) {
		this.value = object;
	}

	public String toString() {
		return value.toString();
	}
}
