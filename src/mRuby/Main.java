package mRuby;

import java.util.ArrayList;

import org.mruby.Ruby;
import org.mruby.RubyProgram;
import org.mruby.parser.Statement;
import org.mruby.parser.exception.ParseException;
import org.mruby.utils.Utils;

public class Main {
	public static void main(String args[]) {
		Ruby ruby = new Ruby();
		//comments
		test("# this is a comment\nputs \"Hello\" # This is a commend","1puts(\"Hello\",);");
		
		//module definition
//		test("module MyModule\nend\n","");
		//class definition
		test("class Test\nend\nclass Test2\nend\n","2define class(Test) < \\n;define class(Test2) < \\n;");
		test("class Test < Hello\nend\n","1define class(Test) < Hello\\n;");
		test("class Test < Hello\ndef hello?\nend\ndef commit!\nend\nend\n","1define class(Test) < Hello\\n  method(hello?):\\n  method(commit!):\\n;");
		test("class Test < Hello\ndef hello\nend\nend\n","1define class(Test) < Hello\\n  method(hello):\\n;");
		test("class Test < Hello\ndef hello\nend\ndef hi(message, message2)\nend\nend\n","1define class(Test) < Hello\\n  method(hello):\\n  method(hi):message message2 \\n;");
		test("class Test < Hello\ndef hello(param = 'test')\nend\nend\n","1define class(Test) < Hello\\n  method(hello):param['test'] \\n;");
		test("class Test < Hello\ndef hello(param = 'test', param2 = 'test2')\nend\nend\n","1define class(Test) < Hello\\n  method(hello):param['test'] param2['test2'] \\n;");
		test("puts \'hello world\'","1puts('hello world',);");
		test("puts \"hello world\"","1puts(\"hello world\",);");
		test("puts \"hello world\\\"\"","1puts(\"hello world\"\",);");
		test("puts \"hello world\", \"hi\"","1puts(\"hello world\",\"hi\",);"); // function call multiple parameters
		test("class Test\ndef hello\nputs 'hello'\nend\nend\n","1define class(Test) < \\n  method(hello):puts('hello',);\\n;");
		test("puts;","1puts();");
		// String escape sequence
		test("test.puts \"Hello World\\n\"", "1test.puts(\"Hello World\n\",);");
		test("test.puts \"Hello World\\\\\"", "1test.puts(\"Hello World\\\",);");
		test("test.puts \"Hello World\\u162\"", "1test.puts(\"Hello World¢\",);");

		//multiple statements
		test("puts \'hello world\';puts \'hi there!'","2puts('hello world',);puts('hi there!',);");
		test("puts(\"test\").to_s","1puts(\"test\",).to_s();");
		test("@arr.compact.to_s;$global.to_s","2@arr.compact().to_s();$global.to_s();");
		test("arr.compact.to_s","1arr.compact().to_s();");
		test("'1'.to_i","1'1'.to_i();");
		test("puts 1","1puts(1,);");
		test("puts 1_000","1puts(1000,);");
		test("puts arr.to_s;puts(arr.to_s)\nputs arr.to_s(1   ),  'hello'", "3puts(arr.to_s(),);puts(arr.to_s(),);puts(arr.to_s(1,),'hello',);");
		test("puts arr.to_s(hello.print('h'))","1puts(arr.to_s(hello.print('h',),),);");
		
		//lambda call
		test("obj.()","1obj.call();");
		
		//formatting test
		test("hello\n   .world()","1hello().world();");
		test("hello.world\n   .sort()","2;hello.world().sort();");
		test("hello.world.\n   sort()\n   .stub()","2;hello.world().sort().stub();");
		test("puts true","1puts(true(),);");
		test("puts false","1puts(false(),);");
		
		//assignment operator
		test("hello = \"hello world\"\n hi = 2","2hello=\"hello world\";hi=2;");
		test("hello = hello.message","1hello=hello.message();");
		test("hello += \"hi\"; hello -= \"hi\"\nhello=\"hi\"", "3hello+=\"hi\";hello-=\"hi\";hello=\"hi\";");
		
		//not operator
		test("result = !true","1result=!(true());");
		
		//inline if
		test("puts \"hello\" if !true", "1if(!(true()))->puts(\"hello\",);");
		
		//block if
		test("if test\nputs \"hello\"\n; puts hello.name;end\n","1if(test())->{puts(\"hello\",);puts(hello.name(),);};");
		
		//if else block
		test("if test\nputs \"Hello\"\nelse\nputs \"Hi\"\nend\n","1if(test())->{puts(\"Hello\",);} else {puts(\"Hi\",);};");
		
		//if elsif block
		test("if test\nputs \"1\"\nelsif !test('hi')\nputs \"2\"\nelse\ncontinue\nend\n","1if(test())->{puts(\"1\",);} else if(!(test('hi',)))->{puts(\"2\",);} else {continue();};");
		
		//nested block if
		test("if true\nif hello.good?\nputs \n\"hello\"\n end\nend\n", "1if(true())->{if(hello.good?())->{puts();\"hello\";};};");

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
		System.out.println(expression);
		Ruby ruby = new Ruby();
		RubyProgram program = ruby.parse(expression);
		String actual = program.toString();
		if (!actual.equals(expected)) {
			System.out.println("failed:\n");
			System.out.println("    actual:"+ actual +"\n");
			System.out.println("  expected:"+ expected +"\n");
			throw new ExpectationError();
		}
		return true;
	}
}
