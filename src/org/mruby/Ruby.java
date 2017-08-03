package org.mruby;

import java.util.ArrayList;

import org.mruby.parser.Statement;
import org.mruby.parser.Parser;

public class Ruby {
	
	public Ruby() {
		
	}

	public static ArrayList<Statement> parse(String rubyExpression) {
		Parser parser = new Parser();
		ArrayList<Statement> statements = new ArrayList<Statement>();
		int i = 0;
		for(; i < rubyExpression.length(); i++) {
			if (!rubyExpression.endsWith("\n")) {
				i = parser.parseStatement(i, statements, rubyExpression + "\n");
			} else {
				i = parser.parseStatement(i, statements, rubyExpression);
			}
			
		}
		return statements;
	}

	public void printIR() {
		// TODO Auto-generated method stub
		
	}
}
