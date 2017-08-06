package org.mruby.parser;

import java.util.ArrayList;
import java.util.List;

import org.mruby.parser.exception.ParseException;
import org.mruby.utils.Utils;

public class Parser {
	private static final int EVAL_STATEMENT = 1;
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
	private static final int FUNCTION_CALL_ON_LITERAL = 29;
	private static final int WAIT_EOL = 30;
	private static final int CAPTURE_NUMERIC_LITERAL = 31;
	private static final int CAPUTRE_METHOD_CALL_NAME_FIRST_CHAR = 32;
	private static final int FUNCTION_CALL_WAIT_FOR_DOT = 33;
	private static final int ASSIGNMENT_OPERATOR_WAIT_FOR_VALUE = 34;
	private static final int READ_IF_CONDITION = 35;
	private static final int EVAL_IF_BLOCK_CONDITION = 36;
	private static final int BANG_OPERATOR_FUNCTION_CALL = 37;
	private static final int PARSE_MODULE = 38;
	private static final int MODULE_DEFINITION_CLASS_DEFINE_NAME = 39;
	private static final int MODULE_DEFINITION_MODULE_DEFINE_BODY = 40;

	int lineno = 1, col = 0;

	public Pair<Integer, Block> parseStatements(int i, String rubyExpression) {
		ArrayList<Statement> statements = new ArrayList<Statement>();
		int currentContext = EVAL_STATEMENT;
		Expression subject = null;
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			printChar(currentChar);
			switch (currentContext) {
			case EVAL_STATEMENT:
				logPrint("PARSE_STATEMENT");
				if (currentChar == ' ')
					continue;
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					continue;
				}
				if (currentChar == ';') {
					continue;
				}

				if (currentChar == 'c') {
					if (Utils.checkString(rubyExpression, i, "class")) {
						i += 5;
						ClassDefinition classDefinition = new ClassDefinition();
						logPrint("eval class");
						i = parseClassDefinition(classDefinition, i, rubyExpression);
						logPrint("end eval class");
						currentContext = EVAL_STATEMENT;
						Statement command = new Statement(Statement.DEFINE_CLASS);
						command.setDetails(classDefinition);
						statements.add(command);
						continue;
					}
				}
				
				if (currentChar == 'm') {
					if (Utils.checkString(rubyExpression, i, "module")) {
						i += 6;
						ModuleDefinition moduleDefinition = new ModuleDefinition();
						logPrint("eval class");
						i = parseModuleDefinition(moduleDefinition, i, rubyExpression);
						logPrint("end eval class");
						currentContext = EVAL_STATEMENT;
						Statement command = new Statement(Statement.DEFINE_MODULE);
						command.setDetails(moduleDefinition);
						statements.add(command);
					}
				}

				if (currentChar == 'e') {
					if (Utils.checkString(rubyExpression, i, "end")) {
						return new Pair<Integer, Block>(i + 3, new Block(statements, "end"));
					} else if (Utils.checkString(rubyExpression, i, "else")) {
						return new Pair<Integer, Block>(i + 4, new Block(statements, "else"));
					} else if (Utils.checkString(rubyExpression, i, "elsif")) {
						return new Pair<Integer, Block>(i + 5, new Block(statements, "elsif"));
					}
				}

				logPrint("statement eval");
				Pair<Integer, Expression> result = parseExpression(i, subject, rubyExpression, false);
				logPrint("statement end");
				i = result.first();
				subject = result.second();
				 
				Statement command = new Statement(Statement.EVALUATE_EXPRESSION);
				
				command.setDetails(result.second());
				statements.add(command);
				break;
			}
		}
		return new Pair<Integer, Block>(i, new Block(statements));
	}

	private int parseModuleDefinition(ModuleDefinition moduleDefinition, int i, String rubyExpression) {
		// TODO Auto-generated method stub
		int currentContext = PARSE_MODULE;
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			printChar(currentChar);
			switch (currentContext) {
			case PARSE_MODULE:
					logPrint("PARSE_MODULE");
					if (currentChar == ' ')
						continue;
					if (currentChar == '\n') {
						lineno++;
						col = 0;
						continue;
					}
					if (Character.isAlphabetic(currentChar)) {
						currentContext = MODULE_DEFINITION_CLASS_DEFINE_NAME;
						moduleDefinition.appendToName(currentChar);
						continue;
					}
					break;
				case MODULE_DEFINITION_CLASS_DEFINE_NAME:
					logPrint("MODULE_DEFINITION_CLASS_DEFINE_NAME");
					if (currentChar == '\n') {
						currentContext = MODULE_DEFINITION_MODULE_DEFINE_BODY;
						lineno++;
						col = 0;
						continue;
					}
					if (isAlphaOrNumeric(currentChar)) {
						moduleDefinition.appendToName(currentChar);
						continue;
					}
					break;
				case MODULE_DEFINITION_MODULE_DEFINE_BODY:
					logPrint("MODULE_DEFINITION_MODULE_DEFINE_BODY");
					if (currentChar == '\n') continue;
					if (currentChar == 'e') {
						if (Utils.checkString(rubyExpression, i, "end")) {
							return i + 3;
						}
					}
					break;
			}
		}
		return i;
	}

	public Pair<Integer, Expression> parseExpression(int i, Expression subject, String rubyExpression, boolean exitOnNewLine) {
		logPrint("subject " + subject);
		int currentContext = EVAL_STATEMENT;
		FunctionCallDefinition operation = null;
		StringBuffer identifierBuffer = new StringBuffer();
		Expression output = null;

		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			printChar(currentChar);
			switch (currentContext) {
			case EVAL_STATEMENT:
				logPrint("EVAL_STATEMENT");
				if (currentChar == '#') {
					i = parseCommentSection(i, rubyExpression);
					continue;
				}

				if (currentChar == 'i') {
					if (Utils.checkString(rubyExpression, i, "if")) {
						i += 2;
						IfBranch ifOperation = new IfBranch();
						ifOperation.setObject(operation);
						
						operation = ifOperation;
						output = ifOperation;
						if (subject!=null && !(subject instanceof Statement)) {
							ifOperation.setObject(subject);
							subject.addDependent(ifOperation);
							currentContext = READ_IF_CONDITION;
						} else {
							currentContext = EVAL_IF_BLOCK_CONDITION;
						}
						continue;
					}
				}

				if (currentChar == '!') {
					operation = new NotFunction();
					operation.setName("!");
					output = operation;
					currentContext = BANG_OPERATOR_FUNCTION_CALL;
					continue;
				}

				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '.') {
					operation = new FunctionCallDefinition();
					operation.setObject(subject);
					if (subject instanceof FunctionCallDefinition) {
						((FunctionCallDefinition) subject).setNextObject(operation);
					}
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME;
					output = operation;
					continue;
				}
				if (currentChar == '\'') {
					Pair<Integer, ConstantLiteral> result = evalSingleQuoteLiteral(i, rubyExpression);
					i = result.first();
					subject = result.second();
					currentContext = FUNCTION_CALL_ON_LITERAL;
					output = result.second();
					continue;
				}
				if (currentChar == '\"') {
					Pair<Integer, ConstantLiteral> result = evalDoubleQuoteLiteral(i, rubyExpression);
					i = result.first();
					subject = result.second();
					currentContext = FUNCTION_CALL_ON_LITERAL;
					output = result.second();
					continue;
				}
				if (isValidVariableStarterCharacter(currentChar)) {
					currentContext = ASSIGNMENT_OR_CALL;
					identifierBuffer.append(currentChar);
					continue;
				}
				if (Character.isDigit(currentChar)) {
					currentContext = CAPTURE_NUMERIC_LITERAL;
					identifierBuffer = new StringBuffer();
					identifierBuffer.append(currentChar);
					continue;
				}
				break;
			case CAPTURE_NUMERIC_LITERAL:
				logPrint("CAPTURE_NUMERIC_LITERAL");
				if (currentChar == '_') {
					continue;
				}
				if (Character.isDigit(currentChar)) {
					identifierBuffer.append(currentChar);
					continue;
				}

				NumericLiteral numeric = new NumericLiteral(identifierBuffer.toString());
				output = numeric;
				currentContext = FUNCTION_CALL_ON_LITERAL;
			case FUNCTION_CALL_ON_LITERAL:
				logPrint("FUNCTION_CALL_ON_LITERAL");
				if (currentChar == ')') {
					return new Pair<Integer, Expression>(i - 1, output);
				}

				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '.') {
					operation = new FunctionCallDefinition();
					operation.setObject(subject);
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME;
					output = operation;
					continue;
				}
//				if (currentChar == 'i') {
//					if (Utils.checkString(rubyExpression, i, "if")) {
//						currentContext = 
//					}
//				}
				currentContext = WAIT_EOL;
			case WAIT_EOL:
				logPrint("WAIT_EOL");
				if (currentChar == ' ')
					continue;
				
				
				if (currentChar == '\n') {
					lineno++;
					col = 0;
					return new Pair<Integer, Expression>(i, wrapToStatement(output));
				}

				return new Pair<Integer, Expression>(i - 1, output);
			case ASSIGNMENT_OR_CALL:
				logPrint("ASSIGNMENT_OR_CALL");
				if (currentChar == 't') {
					if (Utils.checkString(rubyExpression, i, "true")) {
						output = new TrueLiteral();
						i += "true".length() + 1;
						currentContext = FUNCTION_CALL_W_PARAM;
						continue;
					}
				}

				if (currentChar == 'f') {
					if (Utils.checkString(rubyExpression, i, "false")) {
						output = new FalseLiteral();
						i += "false".length() + 1;
						currentContext = FUNCTION_CALL_W_PARAM;
						continue;
					}
				}

				if (isAlphaOrNumeric(currentChar)) {
					identifierBuffer.append(currentChar);
					continue;
				}

				if (currentChar == '=') {
					operation = new AssignmentOperation();
					operation.setObject(identifierBuffer.toString());
					output = operation;
					currentContext = ASSIGNMENT_OPERATOR_WAIT_FOR_VALUE;
					continue;
				}

				if (currentChar == ';') {
					operation = new FunctionCallDefinition();
					operation.setName(identifierBuffer.toString());
					return new Pair<Integer, Expression>(i, operation);
				}

				if (currentChar == '\n') {
					currentContext = FUNCTION_CALL_WAIT_FOR_DOT;
					operation = new FunctionCallDefinition();
					operation.setName(identifierBuffer.toString());
					output = operation;
					if (exitOnNewLine) return new Pair<Integer, Expression>(i, operation);
					continue;
				}

				if (currentChar == '.') {
					operation = new FunctionCallDefinition();
					operation.setObject(identifierBuffer.toString());
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME_FIRST_CHAR;
					output = operation;
					continue;
				} else {
					operation = new FunctionCallDefinition();
					operation.setName(identifierBuffer.toString());
					identifierBuffer = new StringBuffer();
					output = operation;
				}

				if (currentChar == ' ') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				if (currentChar == '(') {
					currentContext = FUNCTION_CALL_W_PARAM_START_PAREN;
					continue;
				}
				break;
			case ASSIGNMENT_OPERATOR_WAIT_FOR_VALUE:
				logPrint("ASSIGNMENT_OPERATOR_WAIT_FOR_VALUE");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					continue;
				}

				logPrint("eval");
				Pair<Integer, Expression> result = parseExpression(i, subject, rubyExpression, exitOnNewLine);
				logPrint("end");
				i = result.first();
				subject = result.second();
				operation.addParam(new FunctionCallParam(result.second()));
				return new Pair<Integer, Expression>(i, output);
			case FUNCTION_CALL_WAIT_FOR_DOT:
				logPrint("FUNCTION_CALL_WAIT_FOR_DOT");
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					continue;
				}
				if (currentChar == '.') {
					FunctionCallDefinition newfunctionCallDefinition = new FunctionCallDefinition();
					newfunctionCallDefinition.setObject(operation);
					operation.setNextObject(newfunctionCallDefinition);
					newfunctionCallDefinition.setName(identifierBuffer.toString());
					output = newfunctionCallDefinition;
					operation = newfunctionCallDefinition;
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME_FIRST_CHAR;
					continue;
				}
				return new Pair<Integer, Expression>(i - 1, output);
			case CAPUTRE_METHOD_CALL_NAME_FIRST_CHAR:
				logPrint("CAPUTRE_METHOD_CALL_NAME_FIRST_CHAR");
				if (currentChar == ' ')
					continue;
				if (currentChar == '\n') {
					lineno++;
					continue;
				}
				;

				if (isAlphaOrNumeric(currentChar)) {
					identifierBuffer.append(currentChar);
					currentContext = CAPUTRE_METHOD_CALL_NAME;
					continue;
				}
				if (currentChar == '(') {
					operation.setName("call");
					currentContext = FUNCTION_CALL_W_PARAM_START_PAREN;
					continue;
				}
			case CAPUTRE_METHOD_CALL_NAME:
				logPrint("CAPUTRE_METHOD_CALL_NAME");
				if (isAlphaOrNumeric(currentChar)) {
					identifierBuffer.append(currentChar);
					continue;
				}

				if (validMethodLastCharacter(currentChar)) {
					identifierBuffer.append(currentChar);
					System.out.println("set name" + identifierBuffer.toString());
					operation.setName(identifierBuffer.toString());
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}

				System.out.println("set name" + identifierBuffer.toString());
				operation.setName(identifierBuffer.toString());

				if (currentChar == '.') {
					FunctionCallDefinition newFunctionCallDefinition = new FunctionCallDefinition();
					operation.setNextObject(newFunctionCallDefinition);
					newFunctionCallDefinition.setObject(operation);
					operation = newFunctionCallDefinition;
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME_FIRST_CHAR;
					output = newFunctionCallDefinition;
				}

				if (currentChar == ' ') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				if (currentChar == '(') {
					currentContext = FUNCTION_CALL_W_PARAM_START_PAREN;
					continue;
				}

				if (currentChar == '\n' || currentChar == ';') {
					return new Pair<Integer, Expression>(i - 1, output);
				}
				break;
			case FUNCTION_CALL_W_PARAM_START_PAREN:
				logPrint("FUNCTION_CALL_W_PARAM_START_PAREN");
			case FUNCTION_CALL_W_PARAM:
				logPrint("FUNCTION_CALL_W_PARAM");

				if (currentChar == '=') {
					AssignmentOperation assignmentOperation = new AssignmentOperation();
					operation.setReference(true);
					assignmentOperation.setObject(operation);
					output = assignmentOperation;
					operation = assignmentOperation;
					currentContext = ASSIGNMENT_OPERATOR_WAIT_FOR_VALUE;
					continue;
				}

				if (currentChar == '-') {
					if (Utils.checkString(rubyExpression, i, "-=")) {
						i += 2;
						SubAssignmentOperation assignmentOperation = new SubAssignmentOperation();
						operation.setReference(true);
						assignmentOperation.setObject(operation);
						output = assignmentOperation;
						operation = assignmentOperation;
						currentContext = ASSIGNMENT_OPERATOR_WAIT_FOR_VALUE;
						continue;
					}
				}

				if (currentChar == '+') {
					if (Utils.checkString(rubyExpression, i, "+=")) {
						i += 2;
						AddAssignmentOperation assignmentOperation = new AddAssignmentOperation();
						operation.setReference(true);
						assignmentOperation.setObject(operation);
						output = assignmentOperation;
						operation = assignmentOperation;
						currentContext = ASSIGNMENT_OPERATOR_WAIT_FOR_VALUE;
						continue;
					}
				}

				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == 'i') {
					if (Utils.checkString(rubyExpression, i, "if")) {
						i += 2;
						IfBranch ifOperation = new IfBranch();
						ifOperation.setObject(operation);
						operation = ifOperation;
						output = ifOperation;
						currentContext = READ_IF_CONDITION;
						continue;
					}
				}

				if (currentChar == '\n') {
					if (currentContext == FUNCTION_CALL_W_PARAM_START_PAREN)
						continue;
					return new Pair<Integer, Expression>(i, output);
				}
				if (currentChar == ')') {
					if (currentContext == FUNCTION_CALL_W_PARAM_START_PAREN) {
						return new Pair<Integer, Expression>(i, output);
					}
					throw new ParseException(col, lineno, "unexpected )");
				}

				if (currentContext == FUNCTION_CALL_W_PARAM_START_PAREN) {
					currentContext = FUNCTION_CALL_W_NEXT_PARAM_PAREN;
				} else {
					currentContext = FUNCTION_CALL_W_NEXT_PARAM;
				}

				logPrint("eval");
				result = parseExpression(i, subject, rubyExpression, exitOnNewLine);
				logPrint("end");
				i = result.first();
				subject = result.second();
				if (subject instanceof Statement) {
					operation.addParam(new FunctionCallParam(result.second()));
					return new Pair<Integer, Expression>(i, wrapToStatement(operation));
				} else {
					operation.addParam(new FunctionCallParam(result.second()));
				}
				break;
			case BANG_OPERATOR_FUNCTION_CALL:
				if (currentChar == ' ') {
					continue;
				}
				logPrint("eval");
				result = parseExpression(i, subject, rubyExpression, exitOnNewLine);
				logPrint("end");
				i = result.first();
				subject = result.second();
				operation.addParam(new FunctionCallParam(result.second()));
				return new Pair<Integer, Expression>(i, output);
			case FUNCTION_CALL_W_NEXT_CALL:
				logPrint("FUNCTION_CALL_W_NEXT_CALL");
				if (currentChar == '.') {
					currentContext = FUNCTION_CALL_W_PARAM;
					FunctionCallDefinition newFunctionCallDefinition = new FunctionCallDefinition();
					newFunctionCallDefinition.setObject(operation);
					operation = newFunctionCallDefinition;
					identifierBuffer = new StringBuffer();
					currentContext = CAPUTRE_METHOD_CALL_NAME;
					output = newFunctionCallDefinition;
					continue;
				}
				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == ',' || currentChar == ')') {
					return new Pair<Integer, Expression>(i - 1, output);
				}
				if (currentChar == '\n') {
					return new Pair<Integer, Expression>(i, output);
				}
			case FUNCTION_CALL_W_NEXT_PARAM_PAREN:
				logPrint("FUNCTION_CALL_W_NEXT_PARAM_PAREN");
			case FUNCTION_CALL_W_NEXT_PARAM:
				logPrint("FUNCTION_CALL_W_NEXT_PARAM");
				if (currentChar == '#') {
					i = parseCommentSection(i, rubyExpression);
					return new Pair<Integer, Expression>(i, output);
				}
				if (currentChar == 'e') {
					if (Utils.checkString(rubyExpression, i, "end")) {
						return new Pair<Integer, Expression>(i - 1, output);
					}
					if (Utils.checkString(rubyExpression, i, "else")) {
						return new Pair<Integer, Expression>(i - 1, output);
					}
					if (Utils.checkString(rubyExpression, i, "elsif")) {
						return new Pair<Integer, Expression>(i - 1, output);
					}
				}

				if (currentChar == ' ') {
					continue;
				}
				if (currentChar == '\n') {
					return new Pair<Integer, Expression>(i, output);
				}
				if (currentChar == ',') {
					currentContext = FUNCTION_CALL_W_PARAM;
					continue;
				}
				if (currentChar == ';') {
					Statement statement = wrapToStatement(output);
					return new Pair<Integer, Expression>(i, statement);
				}
				if (currentChar == ')') {
					if (currentContext == FUNCTION_CALL_W_NEXT_PARAM_PAREN) {
						currentContext = FUNCTION_CALL_W_NEXT_CALL;
						continue;
					}
					throw new ParseException(col, lineno, "unexpected token )");
				}
				if (currentChar == 'i') {
					if (Utils.checkString(rubyExpression, i, "if")) {
						i += 2;
						IfBranch ifOperation = new IfBranch();
						ifOperation.setObject(operation);
						operation = ifOperation;
						output = ifOperation;
						currentContext = READ_IF_CONDITION;
						continue;
					}
				}
				break;
			case READ_IF_CONDITION:
				logPrint("READ_IF_CONDITION");
				logPrint("eval");
				result = parseExpression(i, null, rubyExpression, false);
				logPrint("end");
				i = result.first();
				subject = result.second();
				operation.addParam(new FunctionCallParam(result.second()));
				return new Pair<Integer, Expression>(i, output);
			case EVAL_IF_BLOCK_CONDITION:
				logPrint("EVAL_IF_BLOCK_CONDITION");
				logPrint("EVAL_IF_BLOCK_CONDITION eval");
				result = parseExpression(i, null, rubyExpression, true);
				logPrint("EVAL_IF_BLOCK_CONDITION end");
				i = result.first();
				subject = result.second();
				operation.addParam(new FunctionCallParam(result.second()));
				logPrint("READ_IF_BODY");

				Pair<Integer, Block> statementResult = parseStatements(i, rubyExpression);
				i = statementResult.first();
				operation.setObject(statementResult.second());
				currentContext = PARSE_EXPRESSION;
				
				if (statementResult.second().getEndString().equals("else")) {
					logPrint("READ_ELSE_BODY");
					Pair<Integer, Block> elseStatementResult = parseStatements(i, rubyExpression);
					i = elseStatementResult.first();
					operation.setAltObject(elseStatementResult.second());
				} else if (statementResult.second().getEndString().equals("elsif")) {
					logPrint("READ_ELSEIF");
					IfBranch ifOperation = new IfBranch();
					operation.setAltObject(ifOperation);
					operation = ifOperation;
					currentContext = EVAL_IF_BLOCK_CONDITION;
				}

				break;
			}
		}
		return new Pair<Integer, Expression>(i, output);
	}

	private Statement wrapToStatement(Expression output) {
		Statement statement = new Statement(Statement.EVALUATE_EXPRESSION);
		statement.setDetails(output);
		return statement;
	}

	private int parseCommentSection(int i, String rubyExpression) {
		for (; i < rubyExpression.length(); i++) {
			col++;
			char currentChar = rubyExpression.charAt(i);
			printChar(currentChar);
			logPrint("COMMENT_SECTION");
			if (currentChar == '\n') {
				return i;
			}
			;
		}
		return i;
	}

	private Pair<Integer, ConstantLiteral> evalDoubleQuoteLiteral(int i, String rubyExpression) {
		InterpolatedStringDefinition interpString = new InterpolatedStringDefinition();
		i = parseStringLiteral(interpString, i + 1, rubyExpression);
		return new Pair<Integer, ConstantLiteral>(i, interpString);
	}

	private Pair<Integer, ConstantLiteral> evalSingleQuoteLiteral(int i, String rubyExpression) {
		ConstantStringDefinition constantString = new ConstantStringDefinition();
		i = parseStringLiteral(constantString, i + 1, rubyExpression);
		return new Pair<Integer, ConstantLiteral>(i, constantString);
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
						currentContext = EVAL_STATEMENT;
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
				Pair<Integer, Block> statementResult = parseStatements(i, rubyExpression);
				i = statementResult.first();
				methodDefinition.statements = statementResult.second().getStatements();
				break;
			}
		}
		return i;
	}

	private void printChar(char currentChar) {
		if (currentChar == '\n') {
			System.out.print("\\n ");
		} else {
			System.out.print(currentChar + "  ");
		}
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
		return Character.isAlphabetic(c) || c == '_' || c == '@' || c == '$';
	}

	private boolean validMethodLastCharacter(char c) {
		return c == '!' || c == '?';
	}

	private void logPrint(String string) {
		System.out.println(string);
	}

}
