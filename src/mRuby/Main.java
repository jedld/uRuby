package mRuby;

import java.util.ArrayList;

import org.mruby.Ruby;
import org.mruby.parser.Command;
import org.mruby.parser.exception.ParseException;
import org.mruby.utils.Utils;

public class Main {
	public static void main(String args[]) {
		Ruby ruby = new Ruby();
		test("class Test\nend\nclass Test2\nend\n","define class(Test) < \\n\\ndefine class(Test2) < \\n\\n");
		test("class Test < Hello\nend\n","define class(Test) < Hello\\n\\n");
		test("class Test < Hello\ndef hello\nend\nend\n","define class(Test) < Hello\\n  method(hello):\\n\\n");
		test("class Test < Hello\ndef hello\nend\ndef hi(message, message2)\nend\nend\n","define class(Test) < Hello\\n  method(hello):\\n  method(hi):message message2 \\n\\n");
		test("class Test < Hello\ndef hello(param = 'test')\nend\nend\n","define class(Test) < Hello\\n  method(hello):param[test] \\n\\n");
		test("class Test < Hello\ndef hello(param = 'test', param2 = 'test2')\nend\nend\n","define class(Test) < Hello\\n  method(hello):param[test] param2[test2] \\n\\n");
		test("puts \'hello world\'","call local puts:hello world \\n");
//		System.out.print(Utils.printCommands(commands));
//		commands = ruby.parse("class Test < Hello\ndef hello\nend\nprotected\ndef hi message, message2\nend\nend\n");
//		System.out.print(Utils.printCommands(commands));
//		try {
//		commands = ruby.parse("class Test < Hello\ndef hello\nend\nprotected\ndef hi(message, message2\nend\nend\n");
//		} catch (ParseException e) {
//			
//		}
//		System.out.print(Utils.printCommands(commands));
		ruby.printIR();
	}

	public static boolean test(String expression, String expected) {
		Ruby ruby = new Ruby();
		ArrayList<Command> commands = ruby.parse(expression);
		String actual = Utils.printCommands(commands);
		if (!actual.equals(expected)) {
			System.out.println("failed:\n");
			System.out.println("    actual:"+ actual +"\n");
			System.out.println("  expected:"+ expected +"\n");
			throw new ExpectationError();
		}
		return true;
	}
}
