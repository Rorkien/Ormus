package com.ormus.osl.elements;

import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ormus.ormusgame.Application;
import com.ormus.osl.Runtime;
import com.ormus.shared.gamestate.Snapshot;

public class Query {
	
	public static String doQuery(String query) {
		//Ponto de início é sempre um objeto da classe Snapshot
		Class<?> currentClass = Snapshot.class;
		Object currentObject = Runtime.getRuntime().getClient().getCurrentSnapshot();
		
		String[] methodTree = query.split("\\.");
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
		if (!currentObject.toString().equals(currentObject.hashCode())) return currentObject.toString();
		else return null;
	}
}