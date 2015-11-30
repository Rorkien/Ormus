package com.ormus.osl.elements.operators;

import net.objecthunter.exp4j.operator.Operator;

public class LesserEqualOperator extends Operator {
	
    public LesserEqualOperator() {
		super("<=", 2, true, Operator.PRECEDENCE_POWER + 1);
	}

    @Override
	public double apply(double... values) {
        if (values[0] <= values[1]) return 1d;
        else return 0d;
    }
}
