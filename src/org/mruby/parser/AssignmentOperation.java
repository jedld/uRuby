package org.mruby.parser;

public class AssignmentOperation extends FunctionCallDefinition {
	public String toString() {
		return getObject().toString() + "=" + this.getParams().get(0).toString();
	}
}
