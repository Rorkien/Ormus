package com.ormus.osl.elements;

import com.ormus.osl.Runtime;
import com.ormus.osl.exceptions.ParsingException;

public class Assignment implements Expression {
	String variable;
	String formula;
	
	public Assignment(String variable, String formula) {
		this.variable = variable;
		this.formula = formula;
	}
	
	public Assignment(String assignment) {
		String[] splitAssignment = assignment.split("=");
		
		this.variable = splitAssignment[0].trim();
		this.formula = splitAssignment[1].trim();
	}

	public void execute() throws ParsingException {		
		Runtime.getRuntime().setVariable(variable, new Formula(formula).getValue());
	}
}