package org.mruby.parser;

public class Statement extends Expression {
	public static final int DEFINE_CLASS = 1;
	public static final int CALL_SELF_FUNCTION = 2;
	public static final int CALL_OBJECT_FUNCTION = 3;
	public static final int EVALUATE_EXPRESSION = 4;
	public static final int DEFINE_MODULE = 5;
	private int code;
	private Expression details;

	public Statement(int command) {
		this.code = command;
	}

	public void setDetails(Expression details) {
		this.details = details;
	}

	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		if (code == DEFINE_CLASS) {
			ClassDefinition definition = (ClassDefinition) details;
			strBuilder.append(
					"define class(" + definition.name.toString() + ") < " + definition.extendName.toString() + "\\n");
			for (MethodDefinition method : definition.methods) {
				strBuilder.append("  ");
				if (method.getSection() == MethodDefinition.CLASS_SECTION_PRIVATE) {
					strBuilder.append("private ");
				} else if (method.getSection() == MethodDefinition.CLASS_SECTION_PROTECTED) {
					strBuilder.append("protected ");
				}
				strBuilder.append("method(" + method.name.toString() + "):");
				for (ParameterDefinition parameter : method.parameters) {
					strBuilder.append(parameter.name.toString());
					if (parameter.getDefaultValue() != null) {
						strBuilder.append("[" + parameter.getDefaultValue().toString() + "]");
					}
					strBuilder.append(" ");
				}
				for (Statement statement : method.statements) {
					strBuilder.append(statement.toString() + ";");
				}
				strBuilder.append("\\n");
			}
		} else if (code == DEFINE_MODULE) {
			strBuilder.append(details.toString());
		} else if (code == EVALUATE_EXPRESSION) {
			if (!details.hasDependentExpressions()) {
				if (details instanceof FunctionCallDefinition) {
					FunctionCallDefinition definition = (FunctionCallDefinition) details;
					if (definition.nextObject == null) {
						strBuilder.append(definition.toString());
					}
				} else {
					strBuilder.append(details.toString());
				}
			}
		} else {
			throw new RuntimeException();
		}
		return strBuilder.toString();
	}
}
