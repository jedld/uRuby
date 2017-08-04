package org.mruby.parser;

public class IfBranch extends FunctionCallDefinition {
	public String toString() {
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("if(" + this.getParams().get(0).toString()+")->" + this.object.toString());
		if (altObject!=null) {
			strBuffer.append(" else " + altObject.toString());
		}
		return strBuffer.toString();
	}
}
