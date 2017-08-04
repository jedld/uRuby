package org.mruby;

import java.util.ArrayList;

import org.mruby.parser.Statement;
import org.mruby.parser.Block;
import org.mruby.parser.Pair;
import org.mruby.parser.Parser;

public class Ruby {
	
	public Ruby() {
		
	}

	public static RubyProgram parse(String rubyExpression) {
		Parser parser = new Parser();
		int i = 0;
		RubyProgram program = new RubyProgram();
		for(; i < rubyExpression.length(); i++) {
			if (!rubyExpression.endsWith("\n")) {
				Pair<Integer, Block> result = parser.parseStatements(i,  rubyExpression + "\n");
				i = result.first();
				program.addBlock(result.second());
			} else {
				Pair<Integer, Block> result = parser.parseStatements(i, rubyExpression);
				i = result.first();
				program.addBlock(result.second());
			}
		}
		return program;
	}

	public void printIR() {
		// TODO Auto-generated method stub
		
	}
}
