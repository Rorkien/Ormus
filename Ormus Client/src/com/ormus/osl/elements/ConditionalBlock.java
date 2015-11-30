package com.ormus.osl.elements;

import com.ormus.osl.exceptions.ParsingException;

public class ConditionalBlock implements Expression {
	Conditional condition;
	CodeBlock block;
	
	public ConditionalBlock(Conditional condition, CodeBlock block) throws ParsingException {
		this.condition = condition;
		this.block = block;		
	}

	public void execute() throws ParsingException {
		if (condition.isMet()) block.execute();
	}
}
