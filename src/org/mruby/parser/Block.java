package org.mruby.parser;

import java.util.ArrayList;

public class Block {

	private ArrayList<Statement> statements;
	private String endString = "end";

	public Block(ArrayList<Statement> statements) {
		this.statements = statements;
	}

	public Block(ArrayList<Statement> statements, String endString) {
		this.statements = statements;
		this.endString  = endString;
	}

	public String getEndString() {
		return this.endString;
	}
	
	public String toString() {
		return toString(true);
	}

	public ArrayList<Statement> getStatements() {
		return statements;
	}

	public String toString(boolean printBraces) {
		StringBuffer buffer = new StringBuffer();
		if (printBraces) buffer.append("{");
		for(Statement statement : statements) {
			buffer.append(statement.toString() + ";");
		}
		if (printBraces) buffer.append("}");
		return buffer.toString();
	}
}
