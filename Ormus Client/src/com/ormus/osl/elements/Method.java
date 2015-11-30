package com.ormus.osl.elements;

import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ormus.ormusgame.Application;
import com.ormus.osl.Runtime;
import com.ormus.osl.exceptions.ParsingException;
import com.ormus.shared.entity.Action;
import com.ormus.shared.entity.actions.AttackAction;
import com.ormus.shared.entity.actions.DropAction;
import com.ormus.shared.entity.actions.EquipAction;
import com.ormus.shared.entity.actions.MoveAction;
import com.ormus.shared.entity.actions.PickAction;
import com.ormus.shared.entity.actions.UseAction;
import com.ormus.shared.gamestate.AuthenticationSignal;
import com.ormus.shared.gamestate.CreateSignal;
import com.ormus.shared.gamestate.LogoutSignal;
import com.ormus.shared.gamestate.Snapshot;
import com.ormus.shared.gamestate.StopSignal;

public class Method implements Expression {
	String method;
	String[] parameters;
	
	public Method(String method) {
		this.method = method;
	}
	
	public Method(String method, String... parameter) {
		this.method = method;
		this.parameters = parameter;
	}

	public void execute() throws ParsingException {
		String[] processedParameters = new String[parameters.length];
		
		for (int i = 0; i < processedParameters.length; i++) {
			if (Pattern.matches("^[^\"]\\D[\\d\\D][^\"]*", parameters[i])) {
				Object preprocessor = Runtime.getRuntime().getVariable(parameters[i]);
				if (preprocessor == null) throw new ParsingException("Variable not found: " + parameters[i]);
				processedParameters[i] = parameters[i].replaceAll(parameters[i], preprocessor.toString());
			} else {
				processedParameters[i] = parameters[i].replaceAll("\"", "");
			}
		}
		
		if (method.equals("debugtest")) Application.out.println("OSL RULES!");	
		else if (method.equals("printc")) Application.out.println(processedParameters[0]);
		else if (method.equals("connect")) Runtime.getRuntime().getClient().send(new AuthenticationSignal(processedParameters[0], processedParameters[1], processedParameters[2]));
		else if (method.equals("create")) Runtime.getRuntime().getClient().send(new CreateSignal(processedParameters[0], processedParameters[1], processedParameters[2]));
		else if (method.equals("logout")) Runtime.getRuntime().getClient().send(new LogoutSignal());
		else if (method.equals("stop")) Runtime.getRuntime().getClient().send(new StopSignal());	
		else if (method.equals("move")) Runtime.getRuntime().getClient().send(new MoveAction(Integer.valueOf(processedParameters[0])));
		else if (method.equals("pick")) Runtime.getRuntime().getClient().send(new PickAction(processedParameters[0].equals("") ? Action.CURRENT : Integer.valueOf(processedParameters[0])));
		else if (method.equals("item")) Runtime.getRuntime().getClient().send(new UseAction(Integer.valueOf(processedParameters[0])));
		else if (method.equals("equip")) Runtime.getRuntime().getClient().send(new EquipAction(Integer.valueOf(processedParameters[0])));
		else if (method.equals("attack")) Runtime.getRuntime().getClient().send(new AttackAction());
		else if (method.equals("drop")) Runtime.getRuntime().getClient().send(new DropAction(Integer.valueOf(processedParameters[0])));
		else if (method.equals("reflect")) {
			
			//Ponto de início é sempre um objeto da classe Snapshot
			Class<?> currentClass = Snapshot.class;
			Object currentObject = Runtime.getRuntime().getClient().getCurrentSnapshot();
			
			String[] methodTree = processedParameters[0].split("\\.");
			for (String methodName : methodTree) {
				String getterMethodName = String.format("get%s", methodName.replaceAll("\\[(.*)\\]", ""));
				
				Pattern arrayPattern = Pattern.compile(".*\\[(.*)\\]");
				Matcher arrayMatcher = arrayPattern.matcher(methodName);
				boolean isArray = arrayMatcher.matches();
				
				try {
					java.lang.reflect.Method[] methods = currentClass.getMethods();
					java.lang.reflect.Method method = null;
					for (java.lang.reflect.Method currentMethod : methods) {
						if (currentMethod.getName().equalsIgnoreCase(getterMethodName)) {
							method = currentMethod;
							break;
						}
					}
					
					Class<?> returnType = method.getReturnType();
					Object obj = method.invoke(currentObject);

					currentClass = returnType;
					currentObject = obj;
					
					if (isArray) {
						currentObject = Array.get(currentObject, Integer.valueOf(arrayMatcher.group(1)));
					}
					
				} catch (Exception e) {
					Application.out.println(String.format("Error accessing %s (%s)", methodName, e.getMessage()));
					break;
				}
			}
			if (!currentObject.toString().equals(currentObject.hashCode())) Application.out.println(currentObject);			
		}
	}
}
