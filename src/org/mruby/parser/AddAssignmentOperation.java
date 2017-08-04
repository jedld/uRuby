package org.mruby.parser;

public class AddAssignmentOperation extends AssignmentOperation {
	public String toString() {
		return getObject().toString() + "+=" + this.getParams().get(0).toString();
	}
}
