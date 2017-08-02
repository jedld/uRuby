package org.mruby.parser;

public class Command {
	public static final int DEFINE_CLASS = 1;
	public static final int CALL_SELF_FUNCTION = 2;
	private int code;
	private Object details;

	public Command(int command) {
		this.code = command;
	}

	public void setDetails(Object details) {
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
				strBuilder.append("\\n");
			}
		} else if (code == CALL_SELF_FUNCTION) {
			FunctionCallDefinition definition = (FunctionCallDefinition) details;
			strBuilder.append("call local " + definition.getName() + ":");
			for(FunctionCallParam param : definition.getParams()) {
				strBuilder.append(param.toString() + " ");
			}
		}
		return strBuilder.toString();
	}
}
