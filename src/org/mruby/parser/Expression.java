package org.mruby.parser;

import java.util.ArrayList;

public class Expression {
	protected ArrayList<Expression> dependentExpressions = new ArrayList<Expression>();
	
	public void addDependent(IfBranch ifOperation) {
		dependentExpressions.add(ifOperation);
	}
	
	boolean hasDependentExpressions() {
		if (dependentExpressions.size() > 0) return true;
		return false;
	}
}
