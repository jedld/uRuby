package mRuby;

import java.util.ArrayList;

import org.mruby.Ruby;
import org.mruby.parser.Command;
import org.mruby.utils.Utils;

public class Main {
	public static void main(String args[]) {
		Ruby ruby = new Ruby();
		ArrayList<Command> commands = ruby.parse("class Test\nend\n");
		System.out.print(Utils.printCommands(commands));
		commands = ruby.parse("class Test < Hello\nend\n");
		System.out.print(Utils.printCommands(commands));
		commands = ruby.parse("class Test < Hello\ndef hello\nend\nend\n");
		System.out.print(Utils.printCommands(commands));
		commands = ruby.parse("class Test < Hello\ndef hello\nend\ndef hi(message, message2)\nend\nend\n");
		System.out.print(Utils.printCommands(commands));
		commands = ruby.parse("class Test < Hello\ndef hello\nend\ndef hi message, message2\nend\nend\n");
		System.out.print(Utils.printCommands(commands));
		commands = ruby.parse("class Test < Hello\ndef hello\nend\ndef hi(message, message2\nend\nend\n");
		System.out.print(Utils.printCommands(commands));
		ruby.printIR();
	}

}
