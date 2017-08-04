package org.mruby.parser;

public class SubAssignmentOperation extends AssignmentOperation {
	public String toString() {
		return getObject().toString() + "-=" + this.getParams().get(0).toString();
	}
}
