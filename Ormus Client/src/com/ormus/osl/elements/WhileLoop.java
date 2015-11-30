package com.ormus.osl.elements;

import com.ormus.osl.exceptions.ParsingException;

public class WhileLoop extends Loop {
	Conditional stopCondition;

	public WhileLoop(Conditional stopCondition, CodeBlock block) {
		this.stopCondition = stopCondition;
		this.block = block;
	}

	public void execute() throws ParsingException {
		while (stopCondition.isMet()) {
			block.execute();
			iterations++;
		}		
	}	
}
