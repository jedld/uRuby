package mRuby;

import java.util.ArrayList;

import org.mruby.Ruby;
import org.mruby.parser.Statement;
import org.mruby.parser.exception.ParseException;
import org.mruby.utils.Utils;

public class Main {
	public static void main(String args[]) {
		Ruby ruby = new Ruby();
		test("class Test\nend\nclass Test2\nend\n","2define class(Test) < \\n\\ndefine class(Test2) < \\n\\n");
		test("class Test < Hello\nend\n","1define class(Test) < Hello\\n\\n");
		test("class Test < Hello\ndef hello\nend\nend\n","1define class(Test) < Hello\\n  method(hello):\\n\\n");
		test("class Test < Hello\ndef hello\nend\ndef hi(message, message2)\nend\nend\n","1define class(Test) < Hello\\n  method(hello):\\n  method(hi):message message2 \\n\\n");
		test("class Test < Hello\ndef hello(param = 'test')\nend\nend\n","1define class(Test) < Hello\\n  method(hello):param['test'] \\n\\n");
		test("class Test < Hello\ndef hello(param = 'test', param2 = 'test2')\nend\nend\n","1define class(Test) < Hello\\n  method(hello):param['test'] param2['test2'] \\n\\n");
		test("puts \'hello world\'","1.puts('hello world',)\\n");
		test("puts \"hello world\"","1.puts(\"hello world\",)\\n");
		test("puts \"hello world\\\"\"","1.puts(\"hello world\"\",)\\n");
		test("puts \"hello world\", \"hi\"","1.puts(\"hello world\",\"hi\",)\\n"); // function call multiple parameters
		test("class Test\ndef hello\nputs 'hello'\nend\nend\n","1define class(Test) < \\n  method(hello):.puts('hello',);\\n\\n");
		test("puts;","1.puts()\\n");
		// String escape sequence
		test("test.puts \"Hello World\\n\"", "1test.puts(\"Hello World\n\",)\\n");
		test("test.puts \"Hello World\\\\\"", "1test.puts(\"Hello World\\\",)\\n");
		test("test.puts \"Hello World\\u162\"", "1test.puts(\"Hello World¢\",)\\n");

		//multiple statements
		test("puts \'hello world\';puts \'hi there!'","2.puts('hello world',)\\n.puts('hi there!',)\\n");
		test("puts(\"test\").to_s","1.puts(\"test\",).to_s()\\n");
		test("@arr.compact.to_s","1@arr.compact().to_s()\\n");
		test("arr.compact.to_s","1arr.compact().to_s()\\n");
		test("'1'.to_i","1'1'.to_i()\\n");
		test("puts 1","1.puts(1,)\\n");
		test("puts 1_000","1.puts(1000,)\\n");
		test("puts arr.to_s;puts(arr.to_s)\nputs arr.to_s(1   ),  'hello'", "3.puts(arr.to_s(),)\\n.puts(arr.to_s(),)\\n.puts(arr.to_s(1,),'hello',)\\n");
		test("puts arr.to_s(hello.print('h'))","1.puts(arr.to_s(hello.print('h',),),)\\n");
		
		//lambda call
		test("obj.()","1obj.call()\\n");
		
		//formatting test
		test("hello\n   .world()","1hello.world()\\n");
		test("hello.world\n   .sort()","");
		
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
		System.out.println("---------------------------");
		Ruby ruby = new Ruby();
		ArrayList<Statement> commands = ruby.parse(expression);
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
