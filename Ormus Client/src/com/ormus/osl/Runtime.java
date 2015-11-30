package com.ormus.osl;

import java.util.HashMap;
import java.util.Map;

import com.ormus.ormusgame.networking.Client;

public class Runtime {
	Client client;
	Map<String, Number> variables = new HashMap<String, Number>();
	static Runtime runtime = new Runtime();
	
	private Runtime() {
	}
	
	public static Runtime getRuntime() {
		return runtime;		
	}
	
	public Map<String, Number> getVariables() {
		return variables;
	}
	
	public Object getVariable(String variable) {
		return variables.get(variable);
	}
	
	public void setVariable(String variable, Number value) {
		variables.put(variable, value);
	}
	
	public void unsetVariable(String variable) {
		variables.remove(variable);
	}

	public Client getClient() {
		return client;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
}
