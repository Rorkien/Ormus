package com.ormus.shared.gamestate;

import java.io.Serializable;

public class CreateSignal implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String password;
	private String world;
	
	public CreateSignal(String name, String password, String world) {
		this.name = name;
		this.password = password;
		this.world = world;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}

	public String getWorld() {
		return world;
	}
}
