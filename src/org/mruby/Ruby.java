package org.mruby;

import java.util.ArrayList;

import org.mruby.parser.Command;
import org.mruby.parser.Parser;

public class Ruby {
	
	public Ruby() {
		
	}

	public static ArrayList<Command> parse(String rubyExpression) {
		Parser parser = new Parser();
		return parser.parse(rubyExpression);
	}

	public void printIR() {
		// TODO Auto-generated method stub
		
	}
}
