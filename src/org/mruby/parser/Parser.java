package org.mruby.parser;

import java.util.ArrayList;
import java.util.List;

import org.mruby.parser.exception.ParseException;
import org.mruby.utils.Utils;

public class Parser {
	private static final int EMPTY_CONTEXT = 1;
	private static final int CLASS_DEFINITION_CLASS_W_NAME = 2;
	private static final int CLASS_DEFINITION_CLASS_DEFINE_NAME = 3;
	private static final int CLASS_DEFINITION_CLASS_DEFINE_BODY = 4;
	private static final int CLASS_DEFINITION_CLASS_W_EXTEND = 5;
	private static final int CLASS_DEFINITION_CLASS_EXTEND = 6;
	private static final int CLASS_DEFINITION_CLASS_EXTEND_NAME = 7;
	private static final int DEFINE_METHOD_W_NAME = 8;
	private static final int DEFINE_METHOD_NAME = 9;
	private static final int DEFINE_PARAM_W_NAME = 10;
	private static final int DEFINE_METHOD_BODY = 11;
	private static final int DEFINE_PARAM_W_NAME_START_PAREN = 12;
	private static final int DEFINE_PARAM_NAME = 13;
	private static final int DEFINE_PARAM_W_OPT = 14;
	private static final int PARSE_STRING_LITERAL = 15;
	private static final int DEFINE_PARAM_OPT = 16;
	private static final int ASSIGNMENT_OR_CALL = 17;
	private static final int FUNCTION_CALL_W_PARAM = 18;
	private static final int FUNCTION_CALL = 19;
	private static final int FUNCTION_CALL_W_NEXT_PARAM = 20;


	int lineno = 1, col = 0;

	public ArrayList<Command> parse(String rubyExpression) {
		int currentContext = EMPTY_CONTEXT;
		MethodDefinition methodDefinition = null;
		ParameterDefinition parameterDefinition = null;
		FunctionCallDefinition functionCallDefinition = null;
		StringBuffer identifierBuffer = new StringBuffer();
		ArrayList<Command> commandList = new ArrayList<>();
		for (int i = 0; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			switch (currentContext) {
			case EMPTY_CONTEXT:
				logPrint("EMPTY_CONTEXT");
				if (currentChar == ' ')
					continue;
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					continue;
				}
				if (currentChar == 'c') {
					if (Utils.checkString(rubyExpression, i, "class")) {
						i += "class".length();
						ClassDefinition classDefinition = new ClassDefinition();
						i = parseClassDefinition(classDefinition, i, rubyExpression);
						currentContext = EMPTY_CONTEXT;
						Command command = new Command(Command.DEFINE_CLASS);
						command.setDetails(classDefinition);
						commandList.add(command);
						continue;
					}
				}
				if (Character.isAlphabetic(currentChar)) {
					currentContext = ASSIGNMENT_OR_CALL;
					identifierBuffer.append(currentChar);
					continue;
				}
				break;
			case ASSIGNMENT_OR_CALL:
				logPrint("ASSIGNMENT_OR_CALL");
				if (isAlphaOrNumeric(currentChar)) {
					identifierBuffer.append(currentChar);
					continue;
				}
				
				Command command = new Command(Command.CALL_SELF_FUNCTION);
				functionCallDefinition = new FunctionCallDefinition();
				functionCallDefinition.setName(identifierBuffer.toString());
				identifierBuffer = new StringBuffer();
				command.setDetails(functionCallDefinition);
				commandList.add(command);
				
				if (currentChar == ' ') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				if (currentChar == '\n') {
					currentContext = EMPTY_CONTEXT;
					continue;
				}
				break;
			case FUNCTION_CALL_W_PARAM:
				logPrint("FUNCTION_CALL_W_PARAM");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					currentContext = EMPTY_CONTEXT;
				}
				if (currentChar == '\'') {
					ConstantStringDefinition constantString = new ConstantStringDefinition();
					i = parseStringLiteral(constantString, i + 1, rubyExpression);
					FunctionCallParam functionCallParam = new FunctionCallParam(constantString);
					functionCallDefinition.addParam(functionCallParam);
					currentContext = FUNCTION_CALL_W_NEXT_PARAM;
					continue;
				}
				break;
			case FUNCTION_CALL_W_NEXT_PARAM:
				logPrint("FUNCTION_CALL_W_NEXT_PARAM");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					currentContext = EMPTY_CONTEXT;
					continue;
				}
				if (currentChar == ',') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				break;
			}
		}
		return commandList;
	}

	private int parseClassDefinition(ClassDefinition classDefinition, int index, String rubyExpression) {
		int i = index;
		int currentSection = MethodDefinition.CLASS_SECTION_PUBLIC;
		int currentContext = CLASS_DEFINITION_CLASS_W_NAME;
		MethodDefinition methodDefinition = null;

		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
//			System.out.print(currentChar + " ");
			switch (currentContext) {
			case CLASS_DEFINITION_CLASS_W_NAME:
				logPrint("CLASS_DEFINITION_CLASS_W_NAME");
				if (currentChar == ' ')
					continue;
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					continue;
				}
				if (Character.isAlphabetic(currentChar)) {
					currentContext = CLASS_DEFINITION_CLASS_DEFINE_NAME;
					classDefinition.appendToName(currentChar);
					continue;
				}
				break;
			case CLASS_DEFINITION_CLASS_DEFINE_NAME:
				logPrint("CLASS_DEFINITION_CLASS_DEFINE_NAME");
				if (currentChar == ' ') {
					currentContext = CLASS_DEFINITION_CLASS_W_EXTEND;
					continue;
				}
				if (currentChar == '\n') {
					currentContext = CLASS_DEFINITION_CLASS_DEFINE_BODY;
					lineno++;
					col = 0;
					continue;
				}
				if (isAlphaOrNumeric(currentChar)) {
					classDefinition.appendToName(currentChar);
					continue;
				}
				break;
			case CLASS_DEFINITION_CLASS_W_EXTEND:
				logPrint("CLASS_DEFINITION_CLASS_W_EXTEND");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					currentContext = CLASS_DEFINITION_CLASS_DEFINE_BODY;
					lineno++;
					col = 0;
					continue;
				}
				if (currentChar == '<') {
					if (Utils.checkString(rubyExpression, i, "<")) {
						i++;
						currentContext = CLASS_DEFINITION_CLASS_EXTEND;
						continue;
					}
				}
				break;
			case CLASS_DEFINITION_CLASS_EXTEND:
				logPrint("CLASS_DEFINITION_CLASS_EXTEND");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					continue;
				}
				if (Character.isAlphabetic(currentChar)) {
					currentContext = CLASS_DEFINITION_CLASS_EXTEND_NAME;
					classDefinition.appendToExtend(currentChar);
					continue;
				}
				break;
			case CLASS_DEFINITION_CLASS_EXTEND_NAME:
				logPrint("CLASS_DEFINITION_CLASS_EXTEND_NAME");
				if (currentChar == ' ') {
					currentContext = CLASS_DEFINITION_CLASS_DEFINE_BODY;
					continue;
				}
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					currentContext = CLASS_DEFINITION_CLASS_DEFINE_BODY;
					continue;
				}
				if (isAlphaOrNumeric(currentChar)) {
					classDefinition.appendToExtend(currentChar);
					continue;
				}
				break;
			case CLASS_DEFINITION_CLASS_DEFINE_BODY:
				logPrint("CLASS_DEFINITION_CLASS_DEFINE_BODY");
				if (currentChar == ' ')
					continue;
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					continue;
				}
				if (currentChar == 'd') {
					if (Utils.checkString(rubyExpression, i, "def")) {
						i += "def".length();
						System.out.println("new method " + currentSection);
						methodDefinition = new MethodDefinition(currentSection);
						currentContext = DEFINE_METHOD_W_NAME;
						continue;
					}
				} else if (currentChar == 'e') {
					if (Utils.checkString(rubyExpression, i, "end")) {
						i += "end".length();
						currentContext = EMPTY_CONTEXT;
						return i;
					}
				} else if (currentChar == 'p') {
					if (Utils.checkString(rubyExpression, i, "protected")) {
						i += "protected".length();
						currentSection = MethodDefinition.CLASS_SECTION_PROTECTED;
						continue;
					} else if (Utils.checkString(rubyExpression, i, "private")) {
						i += "private".length();
						currentSection = MethodDefinition.CLASS_SECTION_PRIVATE;
						continue;
					}
				}
				break;
			case DEFINE_METHOD_W_NAME:
				logPrint("DEFINE_METHOD_W_NAME");
				if (currentChar == ' ')
					continue;
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					continue;
				}
				if (Character.isAlphabetic(currentChar)) {
					currentContext = DEFINE_METHOD_NAME;
					methodDefinition.appendToName(currentChar);
					continue;
				}
				break;
			case DEFINE_METHOD_NAME:
				logPrint("DEFINE_METHOD_NAME");
				if (currentChar == ' ') {
					currentContext = DEFINE_PARAM_W_NAME;
					continue;
				}
				if (currentChar == '(') {
					i = parseMethodDefinition(methodDefinition, i + 1, rubyExpression, true);
					currentContext = DEFINE_METHOD_BODY;
					continue;
				}
				if (currentChar == '\n') {
					currentContext = DEFINE_METHOD_BODY;
					continue;
				}
				if (isAlphaOrNumeric(currentChar)) {
					currentContext = DEFINE_METHOD_NAME;
					methodDefinition.appendToName(currentChar);
					continue;
				}
				break;
			case DEFINE_PARAM_W_NAME:
				logPrint("DEFINE_PARAM_W_NAME");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '(') {
					i = parseMethodDefinition(methodDefinition, i + 1, rubyExpression, true);
					currentContext = DEFINE_METHOD_BODY;
					continue;
				}
				if (currentChar == '\n') {
					currentContext = DEFINE_METHOD_BODY;
					continue;
				}
				if (Character.isAlphabetic(currentChar)) {
					i = parseMethodDefinition(methodDefinition, i, rubyExpression, false);
					currentContext = DEFINE_METHOD_BODY;
					continue;
				}
				break;
			case DEFINE_METHOD_BODY:
				logPrint("DEFINE_METHOD_BODY");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					continue;
				}
				if (currentChar == 'e') {
					if (Utils.checkString(rubyExpression, i, "end")) {
						i += "end".length();
						currentContext = CLASS_DEFINITION_CLASS_DEFINE_BODY;
						classDefinition.addMethod(methodDefinition);
						methodDefinition = null;
						continue;
					}
				}
				break;
			}
		}
		return i;
	}

	private int parseMethodDefinition(MethodDefinition methodDefinition, int index, String rubyExpression,
			boolean requireParen) {
		ParameterDefinition parameterDefinition = null;
		int currentContext = DEFINE_PARAM_W_NAME;
		int i = index;
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
//			System.out.print(currentChar + " ");
			switch (currentContext) {
			case DEFINE_PARAM_W_NAME:
				logPrint("DEFINE_PARAM_W_NAME");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					if (requireParen)
						continue;
					return i;
				}
				if (currentChar == ')') {
					if (requireParen)
						return i;
					throw new ParseException(col, lineno, "unexpected )");
				}
				if (Character.isAlphabetic(currentChar)) {
					currentContext = DEFINE_PARAM_NAME;
					parameterDefinition = new ParameterDefinition();
					parameterDefinition.appendToName(currentChar);
					continue;
				}
				break;
			case DEFINE_PARAM_W_OPT:
				logPrint("DEFINE_PARAM_W_OPT");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == ',') {
					currentContext = DEFINE_PARAM_W_NAME;
					continue;
				}
				if (currentChar == '=') {
					currentContext = DEFINE_PARAM_OPT;
					continue;
				}
				if (Character.isAlphabetic(currentChar)) {
					currentContext = DEFINE_PARAM_NAME;
					parameterDefinition = new ParameterDefinition();
					parameterDefinition.appendToName(currentChar);
					continue;
				}
				break;
			case DEFINE_PARAM_NAME:
				logPrint("DEFINE_PARAM_NAME");
				if (currentChar == ' ') {
					methodDefinition.addParameter(parameterDefinition);
					currentContext = DEFINE_PARAM_W_OPT;
					continue;
				}
				if (currentChar == '=') {
					methodDefinition.addParameter(parameterDefinition);
					currentContext = DEFINE_PARAM_OPT;
					continue;
				}
				
				if (currentChar == ',') {
					methodDefinition.addParameter(parameterDefinition);
					currentContext = DEFINE_PARAM_W_NAME;
					continue;
				}
				if (currentChar == '\n') {
					methodDefinition.addParameter(parameterDefinition);
					if (requireParen)
						throw new ParseException(col, lineno, "expected )");
					return i;
				}
				if (currentChar == ')') {
					methodDefinition.addParameter(parameterDefinition);
					if (requireParen)
						return i;
					throw new ParseException(col, lineno, "unexpected )");
				}
				if (isAlphaOrNumeric(currentChar)) {
					parameterDefinition.appendToName(currentChar);
					continue;
				}
				break;
			case DEFINE_PARAM_OPT:
				logPrint("DEFINE_PARAM_OPT");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\'') {
					ConstantStringDefinition constantString = new ConstantStringDefinition();
					i = parseStringLiteral(constantString, i + 1, rubyExpression);
					parameterDefinition.setDefaultValue(constantString);
					currentContext = DEFINE_PARAM_W_NAME;
					continue;
				}
				break;
			}
		}
		return i;
	}

	private int parseStringLiteral(ConstantStringDefinition constantString, int index, String rubyExpression) {
		int currentContext = PARSE_STRING_LITERAL;
		int i = index;
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
//			System.out.print(currentChar + " ");
			switch (currentContext) {
				case PARSE_STRING_LITERAL:
					logPrint("PARSE_STRING_LITERAL");
					if (currentChar == '\'') {
						return i;
					}
					constantString.appendValue(currentChar);
				break;
			}
		}
		return i;
	}

	boolean isAlphaOrNumeric(char c) {
		return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
	}
	
	boolean isValidStarterCharacter(char c) {
		return Character.isAlphabetic(c) || c == '_';
	}

	private void logPrint(String string) {
		 System.out.println(string);
	}

}
