package org.mruby.parser;

import java.util.ArrayList;
import java.util.List;

import org.mruby.parser.exception.ParseException;
import org.mruby.utils.Utils;

public class Parser {
	private static final int PARSE_STATEMENT = 1;
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
	private static final int PARSE_ESCAPE_SEQUENCE = 21;
	private static final int PARSE_ESCAPE_CHARACTER_CODE = 22;
	private static final int DEFINE_METHOD_NAME_MUST_END = 23;
	private static final int CAPUTRE_METHOD_CALL_NAME = 24;
	private static final int FUNCTION_CALL_W_PARAM_START_PAREN = 25;
	private static final int FUNCTION_CALL_W_NEXT_CALL = 26;
	private static final int FUNCTION_CALL_W_NEXT_PARAM_PAREN = 27;
	private static final int PARSE_EXPRESSION = 28;

	int lineno = 1, col = 0;

	public Object parseExpression(int i, ArrayList<Expression> expressions, String rubyExpression) {
		int currentContext = PARSE_EXPRESSION;
		Expression expression = null;
		FunctionCallDefinition functionCallDefinition = null;
		StringBuffer identifierBuffer = new StringBuffer();
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			printChar(currentChar);
			switch (currentContext) {
			case PARSE_EXPRESSION:
				logPrint("PARSE_EXPRESSION");
				if (currentChar == ' ')
					continue;
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					continue;
				}
				if (currentChar == '\'') {
					ConstantStringDefinition constantString = new ConstantStringDefinition();
					i = parseStringLiteral(constantString, i + 1, rubyExpression);
					return constantString;
				}
				if (currentChar == '\"') {
					InterpolatedStringDefinition interpString = new InterpolatedStringDefinition();
					i = parseStringLiteral(interpString, i + 1, rubyExpression);
					return interpString;
				}
				if (isValidVariableStarterCharacter(currentChar)) {
					currentContext = ASSIGNMENT_OR_CALL;
					identifierBuffer.append(currentChar);
					continue;
				}
				break;
			case ASSIGNMENT_OR_CALL:
				
				break;
			}
		}
		return i;
	}

	public int parseStatement(int i, ArrayList<Statement> statements, String rubyExpression) {
		int currentContext = PARSE_STATEMENT;
		FunctionCallDefinition functionCallDefinition = null;
		StringBuffer identifierBuffer = new StringBuffer();
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			printChar(currentChar);
			switch (currentContext) {
			case PARSE_STATEMENT:
				logPrint("PARSE_STATEMENT");
				if (currentChar == ' ')
					continue;
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					return i;
				}
				if (currentChar == ';') {
					return i;
				}
				if (currentChar == 'c') {
					if (Utils.checkString(rubyExpression, i, "class")) {
						i += "class".length();
						ClassDefinition classDefinition = new ClassDefinition();
						i = parseClassDefinition(classDefinition, i, rubyExpression);
						currentContext = PARSE_STATEMENT;
						Statement command = new Statement(Statement.DEFINE_CLASS);
						command.setDetails(classDefinition);
						statements.add(command);
						continue;
					}
				}
				if (isValidVariableStarterCharacter(currentChar)) {
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

				if (currentChar == '.') {
					Statement command = new Statement(Statement.CALL_OBJECT_FUNCTION);
					functionCallDefinition = new FunctionCallDefinition();
					functionCallDefinition.setObject(identifierBuffer.toString());
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME;
					command.setDetails(functionCallDefinition);
					statements.add(command);
					continue;
				} else {
					Statement command = new Statement(Statement.CALL_SELF_FUNCTION);
					functionCallDefinition = new FunctionCallDefinition();
					functionCallDefinition.setName(identifierBuffer.toString());
					identifierBuffer = new StringBuffer();
					command.setDetails(functionCallDefinition);
					statements.add(command);
				}

				if (currentChar == ' ') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				if (currentChar == '(') {
					currentContext = FUNCTION_CALL_W_PARAM_START_PAREN;
					continue;
				}
				if (currentChar == '\n') {
					return i;
				}
				break;
			case CAPUTRE_METHOD_CALL_NAME:
				logPrint("CAPUTRE_METHOD_CALL_NAME");
				if (isAlphaOrNumeric(currentChar)) {
					identifierBuffer.append(currentChar);
					continue;
				}
				System.out.println("set name" + identifierBuffer.toString());
				functionCallDefinition.setName(identifierBuffer.toString());

				if (currentChar == '.') {
					Statement command = new Statement(Statement.CALL_OBJECT_FUNCTION);
					FunctionCallDefinition newFunctionCallDefinition = new FunctionCallDefinition();
					functionCallDefinition.setNextObject(newFunctionCallDefinition);
					newFunctionCallDefinition.setObject(functionCallDefinition);
					functionCallDefinition = newFunctionCallDefinition;
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME;
					command.setDetails(newFunctionCallDefinition);
					statements.add(command);
				}

				if (currentChar == ' ') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				if (currentChar == '(') {
					currentContext = FUNCTION_CALL_W_PARAM_START_PAREN;
					continue;
				}
				if (currentChar == '\n') {
					return i;
				}
				break;
			case FUNCTION_CALL_W_PARAM_START_PAREN:
			case FUNCTION_CALL_W_PARAM:
				logPrint("FUNCTION_CALL_W_PARAM");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					if (currentContext == FUNCTION_CALL_W_PARAM_START_PAREN)
						continue;
					return i;
				}
				if (currentChar == ')') {
					if (currentContext == FUNCTION_CALL_W_PARAM_START_PAREN) {
						currentContext = FUNCTION_CALL_W_NEXT_CALL;
						continue;
					}
				}
				if (currentChar == '\'') {
					ConstantStringDefinition constantString = new ConstantStringDefinition();
					i = parseStringLiteral(constantString, i + 1, rubyExpression);
					FunctionCallParam functionCallParam = new FunctionCallParam(constantString);
					functionCallDefinition.addParam(functionCallParam);
					currentContext = FUNCTION_CALL_W_NEXT_PARAM;
					continue;
				}
				if (currentChar == '\"') {
					InterpolatedStringDefinition interpString = new InterpolatedStringDefinition();
					i = parseStringLiteral(interpString, i + 1, rubyExpression);
					FunctionCallParam functionCallParam = new FunctionCallParam(interpString);
					functionCallDefinition.addParam(functionCallParam);

					if (currentContext == FUNCTION_CALL_W_PARAM_START_PAREN) {
						currentContext = FUNCTION_CALL_W_NEXT_PARAM_PAREN;
					} else {
						currentContext = FUNCTION_CALL_W_NEXT_PARAM;
					}
					continue;
				}
				break;
			case FUNCTION_CALL_W_NEXT_CALL:
				logPrint("FUNCTION_CALL_W_NEXT_CALL");
				if (currentChar == '.') {
					currentContext = FUNCTION_CALL_W_PARAM;
					Statement command = new Statement(Statement.CALL_OBJECT_FUNCTION);
					FunctionCallDefinition newFunctionCallDefinition = new FunctionCallDefinition();
					newFunctionCallDefinition.setObject(functionCallDefinition);
					functionCallDefinition = newFunctionCallDefinition;
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME;
					command.setDetails(newFunctionCallDefinition);
					statements.add(command);
					continue;
				}
				if (currentChar == ' ') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				if (currentChar == '\n') {
					return i;
				}
			case FUNCTION_CALL_W_NEXT_PARAM_PAREN:
				logPrint("FUNCTION_CALL_W_NEXT_PARAM_PAREN");
			case FUNCTION_CALL_W_NEXT_PARAM:
				logPrint("FUNCTION_CALL_W_NEXT_PARAM");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					return i;
				}
				if (currentChar == ',') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				if (currentChar == ';') {
					return i;
				}
				if (currentChar == ')') {
					if (currentContext == FUNCTION_CALL_W_NEXT_PARAM_PAREN) {
						currentContext = FUNCTION_CALL_W_NEXT_CALL;
						continue;
					}
					throw new ParseException(col, lineno, "unexpected token )");
				}
				break;
			}
		}
		return i;
	}

	private int parseClassDefinition(ClassDefinition classDefinition, int index, String rubyExpression) {
		int i = index;
		int currentSection = MethodDefinition.CLASS_SECTION_PUBLIC;
		int currentContext = CLASS_DEFINITION_CLASS_W_NAME;
		MethodDefinition methodDefinition = null;

		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			printChar(currentChar);
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
						// System.out.println("new method " + currentSection);
						methodDefinition = new MethodDefinition(currentSection);
						currentContext = DEFINE_METHOD_W_NAME;
						continue;
					}
				} else if (currentChar == 'e') {
					if (Utils.checkString(rubyExpression, i, "end")) {
						i += "end".length();
						currentContext = PARSE_STATEMENT;
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

				if (isSpecialOperatorNames(currentChar)) {
					currentContext = DEFINE_METHOD_NAME_MUST_END;
					methodDefinition.appendToName(currentChar);
					continue;
				}
				break;
			case DEFINE_METHOD_NAME_MUST_END:
				if (currentChar == '\n') {
					currentContext = DEFINE_METHOD_BODY;
					lineno++;
					continue;
				}
				if (currentChar == ' ') {
					currentContext = DEFINE_PARAM_W_NAME;
					continue;
				}
				if (currentChar == '(') {
					i = parseMethodDefinition(methodDefinition, i + 1, rubyExpression, true);
					currentContext = DEFINE_METHOD_BODY;
					continue;
				}
				throw new ParseException(col, lineno, "!,= or ? can only appear at the end of a method name");
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
				i = parseStatement(i, methodDefinition.statements, rubyExpression);
				break;
			}
		}
		return i;
	}

	private void printChar(char currentChar) {
		System.out.print(currentChar + " ");
	}

	private int parseMethodDefinition(MethodDefinition methodDefinition, int index, String rubyExpression,
			boolean requireParen) {
		ParameterDefinition parameterDefinition = null;
		int currentContext = DEFINE_PARAM_W_NAME;
		int i = index;
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			printChar(currentChar);
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

	private int parseStringLiteral(ConstantLiteral constantString, int index, String rubyExpression) {
		int currentContext = PARSE_STRING_LITERAL;
		int i = index;
		StringBuffer escapeCode = null;
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			final char delimiter = constantString instanceof ConstantStringDefinition ? '\'' : '"';
			printChar(currentChar);
			switch (currentContext) {
			case PARSE_ESCAPE_CHARACTER_CODE:
				logPrint("PARSE_ESCAPE_CHARACTER_CODE");
				if (Character.isDigit(currentChar)) {
					escapeCode.append(currentChar);
					continue;
				}
				constantString.appendCodePoint(Integer.parseInt(escapeCode.toString()));
				currentContext = PARSE_STRING_LITERAL;
			case PARSE_STRING_LITERAL:
				logPrint("PARSE_STRING_LITERAL");
				if (currentChar == delimiter) {
					return i;
				}
				if (currentChar == '\\') {
					currentContext = PARSE_ESCAPE_SEQUENCE;
					continue;
				}
				constantString.appendValue(currentChar);
				break;
			case PARSE_ESCAPE_SEQUENCE:
				logPrint("PARSE_STRING_LITERAL");
				if (currentChar == '\\') {
					constantString.appendValue('\\');
					currentContext = PARSE_STRING_LITERAL;
					continue;
				}
				if (currentChar == '"') {
					constantString.appendValue('"');
					currentContext = PARSE_STRING_LITERAL;
					continue;
				}
				if (currentChar == '\'') {
					constantString.appendValue('\'');
					currentContext = PARSE_STRING_LITERAL;
					continue;
				}
				if (currentChar == 'n') {
					constantString.appendValue('\n');
					currentContext = PARSE_STRING_LITERAL;
					continue;
				}
				if (currentChar == 'r') {
					constantString.appendValue('\r');
					currentContext = PARSE_STRING_LITERAL;
					continue;
				}
				if (currentChar == 't') {
					constantString.appendValue('\t');
					currentContext = PARSE_STRING_LITERAL;
					continue;
				}
				if (currentChar == 'u') {
					currentContext = PARSE_ESCAPE_CHARACTER_CODE;
					escapeCode = new StringBuffer();
					continue;
				}
				// if (Character.isDigit(currentChar)) {
				// currentContext = PARSE_ESCAPE_CHARACTER_CODE;
				// escapeCode = new StringBuffer();
				// continue;
				// }
				break;
			}
		}
		return i;
	}

	boolean isAlphaOrNumeric(char c) {
		return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
	}

	boolean isValidMethodStarterCharacter(char c) {
		return Character.isAlphabetic(c) || c == '_';
	}

	boolean isSpecialOperatorNames(char c) {
		return c == '!' || c == '?' || c == '=';
	}

	boolean isValidVariableStarterCharacter(char c) {
		return Character.isAlphabetic(c) || c == '_' || c == '@';
	}

	private void logPrint(String string) {
		System.out.println(string);
	}

}
