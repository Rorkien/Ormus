package com.ormus.osl.elements;

import java.util.Map;

import net.objecthunter.exp4j.ExpressionBuilder;

import com.ormus.osl.Runtime;
import com.ormus.osl.exceptions.ParsingException;

public class Formula {
	Double result = null;

	public Formula(String expression) throws ParsingException {
		try {
			ExpressionBuilder builder = new ExpressionBuilder(expression);
			Map<String, Number> variables = Runtime.getRuntime().getVariables();

			for (String variable : variables.keySet()) builder.variable(variable);

			net.objecthunter.exp4j.Expression expressionObject = builder.build();		
			for (String variable : variables.keySet()) expressionObject.setVariable(variable, variables.get(variable).doubleValue());

			result = expressionObject.evaluate();
		}
		catch (Exception e) {
			throw new ParsingException("error parsing expression: " + expression);
		}
	}
	
	public double getValue() {
		return result;
	}
	
	public int getIntValue() {
		return result.intValue();
	}

}
