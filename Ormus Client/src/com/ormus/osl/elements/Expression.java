package com.ormus.osl.elements;

import com.ormus.osl.exceptions.ParsingException;

public interface Expression {
	public void execute() throws ParsingException;
}
