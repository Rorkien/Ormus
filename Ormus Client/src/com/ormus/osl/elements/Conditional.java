package com.ormus.osl.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ormus.osl.Runtime;
import com.ormus.osl.elements.operators.EqualOperator;
import com.ormus.osl.elements.operators.GreaterEqualOperator;
import com.ormus.osl.elements.operators.GreaterOperator;
import com.ormus.osl.elements.operators.LesserEqualOperator;
import com.ormus.osl.elements.operators.LesserOperator;
import com.ormus.osl.elements.operators.NotEqualOperator;
import com.ormus.osl.exceptions.ParsingException;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.operator.Operator;

public class Conditional {
	String conditional;
	net.objecthunter.exp4j.Expression expressionObject;
	ExpressionBuilder builder;
	
	List<Operator> logicOperators = new ArrayList<Operator>();
	
	{
		logicOperators.add(new GreaterOperator());
		logicOperators.add(new GreaterEqualOperator());
		logicOperators.add(new LesserOperator());
		logicOperators.add(new LesserEqualOperator());
		logicOperators.add(new EqualOperator());
		logicOperators.add(new NotEqualOperator());
	}
	
	public Conditional(String conditional) throws ParsingException {
		this.conditional = conditional.replace("||", "+").replace("&&", "*");
		
		String[] splitTerms = this.conditional.split(">|>=|<|<=|==|!=|\\+|\\-|\\*|\\/]");
		for (int i = 0; i < splitTerms.length; i++) {
			splitTerms[i] = splitTerms[i].trim();
			
			if (splitTerms[i].startsWith("$")) {
				this.conditional = this.conditional.replace(splitTerms[i], Query.doQuery(splitTerms[i].substring(1, splitTerms[i].length())));				
			}
		}
		
		builder = new ExpressionBuilder(this.conditional);
	}
	
	public boolean isMet() throws ParsingException {
		Map<String, Number> variables = Runtime.getRuntime().getVariables();
		
		for (String variable : variables.keySet()) builder.variable(variable);
		builder.operator(logicOperators);

		try {
			expressionObject = builder.build();
		} catch (IllegalArgumentException e) {
			throw new ParsingException("invalid condition " + conditional + ". Check the numbers and arguments.");
		}
		for (String variable : variables.keySet()) expressionObject.setVariable(variable, variables.get(variable).doubleValue());

		double result;
		try {
			result = expressionObject.evaluate();
		} catch (ArithmeticException e) {
			throw new ParsingException("invalid condition " + conditional + ". Check the numbers and arguments.");
		}
		
		return result > 0;
	}
}
