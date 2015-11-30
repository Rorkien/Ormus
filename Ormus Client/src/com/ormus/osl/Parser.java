package com.ormus.osl;

import com.ormus.osl.elements.CodeBlock;
import com.ormus.osl.exceptions.ParsingException;

public class Parser {
	CodeBlock rootBlock = new CodeBlock();
	
	public static String[] reservedWords = {
		"if",
		"for",
		"while"
	};
		
	public void parse(String[] codeBlock) throws ParsingException {
		StringBuilder trimmedBlock = new StringBuilder();
		for (String s : codeBlock) trimmedBlock.append(s.trim());
		
		rootBlock.parse(trimmedBlock.toString());
	}
	
	public void execute() throws ParsingException {
		rootBlock.execute();
	}
	
	/*
	 * ok Formula(String)
	 * ok Conditional(Condition)
	 * ok 	fazer || (or) e ! (not)
	 * okok Loop
	 * Method()
	 */
	
	/*
	 * Method
	 * Loop
	 * 		Conditional
	 * 			Method
	 * 		Method
	 * 
	 * 
	 */	
	
	/*
	say("testing this code");
	x = 0
	for (i = 0; i < 10; i++) {
		if (i > 0) {
			pickup();
		}
		move(3);
		x = x + 1
	}

	new Method("say", "testing this code").execute();
	Runtime.setVariable("x", 0);
	Runtime.setVariable("i", 0);

	LoopBlock lb = new LoopBlock();
	Loop l = new Loop(new Conditional("i < 10"), lb);

	ConditionalBlock cb = new ConditionalBlock();
	c.add(new Method("pickup", null));
	Conditional c = new Conditional("i > 0", cb);

	l.getBlock.add();
	l.getBlock.add(new Method("move", "3"));
	Runtime.setVariable("x", new Formula("x + 1"));

	l.execute();
	*/
	
}
