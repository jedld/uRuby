package org.mruby.utils;

import java.util.List;

import org.mruby.parser.Statement;

public class Utils {

	public static boolean checkString(String rubyExpression, int index, String string) {
		if (index + string.length() >= rubyExpression.length()) return false;
		String subStr =rubyExpression.substring(index, index + string.length());
		if (subStr.equals(string)) {
			char lastChar = rubyExpression.charAt(index + string.length());
			if (lastChar == ' ' || lastChar == '\n')
			return true;
		}
		return false;
	}

	public static String printCommands(List<Statement> commands) {
		StringBuilder commandsStr =new StringBuilder();
		for(Statement command : commands) {
			commandsStr.append(command.toString() + "\\n");
		}
		return commandsStr.toString();
	}
}
