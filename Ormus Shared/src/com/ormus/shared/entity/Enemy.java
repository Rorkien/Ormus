package com.ormus.shared.entity;

import java.io.Serializable;

public class Enemy extends Creature implements Serializable {	
	private static final long serialVersionUID = 1L;
	private int id;
	private int behaviorId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getBehaviorId() {
		return behaviorId;
	}
	
	public void setBehaviorId(int behaviorId) {
		this.behaviorId = behaviorId;
	}
}
