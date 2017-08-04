package org.mruby.parser;

public class NotFunction extends FunctionCallDefinition {
	public String toString() {
		return "!("+this.params.get(0).toString()+")";
	}
}
