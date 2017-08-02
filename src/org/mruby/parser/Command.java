package org.mruby.parser;

public class Command {
	public static final int DEFINE_CLASS = 1;
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
					"define class(" + definition.name.toString() + ") < " + definition.extendName.toString() + "\n");
			for (MethodDefinition method : definition.methods) {
				strBuilder.append("  ");
				if (method.getSection() == MethodDefinition.CLASS_SECTION_PRIVATE) {
					strBuilder.append("private ");
				} else if (method.getSection() == MethodDefinition.CLASS_SECTION_PROTECTED) {
					strBuilder.append("protected ");
				}
				strBuilder.append("method(" + method.name.toString() + "):");
				for (ParameterDefinition parameter : method.parameters) {
					strBuilder.append(parameter.name.toString() + " ");
				}
				strBuilder.append("\n");
			}
		}
		return strBuilder.toString();
	}
}
