package com.ormus.osl.elements;

import java.util.ArrayList;
import java.util.List;

import com.ormus.osl.Parser;
import com.ormus.osl.exceptions.ParsingException;

public class CodeBlock implements Expression {
	List<Expression> block = new ArrayList<Expression>();
	
	public void addToBlock(Expression e) {
		block.add(e);
	}
	
	public void execute() throws ParsingException {
		for (Expression e : block) {
			e.execute();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}
	}

	public void parse(String codeBlock) throws ParsingException {
		String[] splittedCodeBlock = splitLimiters(codeBlock);
		
		nextExpression:
		for (String s : splittedCodeBlock) {
			
			for (int i = 0; i < Parser.reservedWords.length; i++) {
				if (s.matches("(" + Parser.reservedWords[i] + ").*")) {
					if (Parser.reservedWords[i].equals("if")) {
						//System.out.println("an if");
						
						String arguments = getArgumentsOnParenthesis(s);						
						
						Conditional condition = new Conditional(arguments);
						CodeBlock block = new CodeBlock();
						block.parse(getExpressionOnBracket(s));
						
						addToBlock(new ConditionalBlock(condition, block));
						
					} else if (Parser.reservedWords[i].equals("for")) {
						//System.out.println("a for loop");
						
						String[] arguments = getArgumentsOnParenthesis(s).split(";");
						if (arguments.length < 3) throw new ParsingException("invalid arguments on for-loop declaration");
						
						Assignment declaration = new Assignment(arguments[0]);
						Conditional stopCondition = new Conditional(arguments[1]);
						Assignment increment = new Assignment(arguments[2]);
						CodeBlock block = new CodeBlock();
						block.parse(getExpressionOnBracket(s));
						
						addToBlock(new ForLoop(declaration, stopCondition, increment, block));

					} else if (Parser.reservedWords[i].equals("while")) {
						//System.out.println("a while");
						String condition = getArgumentsOnParenthesis(s);
						
						Conditional stopCondition = new Conditional(condition);
						CodeBlock block = new CodeBlock();
						block.parse(getExpressionOnBracket(s));
						
						addToBlock(new WhileLoop(stopCondition, block));
					}
					
					continue nextExpression;
				}
			}

			if (s.split("=").length > 1) {
				//System.out.println("an variable assignment");
				addToBlock(new Assignment(s));
			} else if (s.contains("(") && s.contains(")")) {
				//System.out.println("calling method");

				String[] arguments = getArgumentsOnParenthesis(s).split(",");
				addToBlock(new Method(getMethodName(s), arguments));
			} else if (s.startsWith("$")){
				//System.out.println("calling preprocessor");
				
				//addToBlock(Query.doQuery(s));
			} else {
				//Coisas inválidas...
			}
		}
	}
	
	public String[] splitLimiters(String codeBlock) throws ParsingException {
		List<String> splitted = new ArrayList<String>();
		
		while (codeBlock.contains(";}")) codeBlock = codeBlock.replace(";}", "};");
		
		int bracketAmount = 0;
		int parenthesisAmount = 0;
		
		char[] chars = codeBlock.toCharArray();
		int startPoint = 0;
		int offset = 0;
		
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			offset++;
			
			if (c == '(') parenthesisAmount++;
			else if (c == ')') {
				parenthesisAmount--;
				if (parenthesisAmount < 0) throw new ParsingException("unbalanced expression.");
			}
			
			else if (c == '{') bracketAmount++;
			else if (c == '}') {
				bracketAmount--;
				if (bracketAmount < 0) throw new ParsingException("unbalanced expression.");
			}
			
			else if (c == ';') {
				if (bracketAmount + parenthesisAmount == 0) {
					splitted.add(new String(chars, startPoint, offset - 1));
					startPoint += offset;
					offset = 0;
				}
			}
			
			if (i == chars.length - 1 && offset > 0) {
				if (bracketAmount + parenthesisAmount > 0) throw new ParsingException("unbalanced expression.");
				
				splitted.add(new String(chars, startPoint, offset));
				break;
			}
		}
		
		return splitted.toArray(new String[splitted.size()]);
	}
	
	public String getExpressionOnBracket(String expression) throws ParsingException {
		int first = expression.indexOf('{') + 1;
		int last = expression.lastIndexOf('}');
		
		if (first < 0 || last < 0) throw new ParsingException("invalid expression: " + expression);

		return expression.substring(first, last);		
	}

	public String getArgumentsOnParenthesis(String arguments) {
		int first = arguments.indexOf('(') + 1;
		int last = 0;

		int parenthesisAmount = 1;
		char[] chars = arguments.toCharArray();

		for (int i = first; i < chars.length; i++) {
			if (chars[i] == '(') parenthesisAmount++;
			else if (chars[i] == ')') {
				parenthesisAmount--;
				if (parenthesisAmount == 0) {
					last = i;
					break;
				}
			}					
		}

		return arguments.substring(first, last);	
	}
	
	public String getMethodName(String expression) throws ParsingException {
		if (expression.indexOf('(') < 0) throw new ParsingException("method " + expression + " with invalid parameters");
		return expression.substring(0, expression.indexOf('('));
	}
}
