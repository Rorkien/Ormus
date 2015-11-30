package com.ormus.osl.elements;

import com.ormus.osl.exceptions.ParsingException;

public class ForLoop extends Loop {
	Assignment declaration;
	Conditional stopCondition;
	Assignment increment;

	public ForLoop(Assignment declaration, Conditional stopCondition, Assignment increment, CodeBlock block) {
		this.declaration = declaration;
		this.stopCondition = stopCondition;
		this.increment = increment;
		this.block = block;
	}

	public void execute() throws ParsingException {
		declaration.execute();
		
		while (stopCondition.isMet()) {
			//System.out.println(com.ormus.osl.Runtime.getRuntime().getVariable("x"));
			
			block.execute();
			increment.execute();
			iterations++;
		}
		
	}	
}
