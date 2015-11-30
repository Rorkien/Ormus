package com.ormus.shared.entity;

import java.io.Serializable;

public class Tile implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;
	private int spriteId;
	private boolean isUsable = false;
	private boolean isWalkable = true;
	private int[] useEffectsId;
	private int[] walkEffectsId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSpriteId() {
		return spriteId;
	}

	public void setSpriteId(int spriteId) {
		this.spriteId = spriteId;
	}

	public boolean isUsable() {
		return isUsable;
	}
	
	public void setUsable(boolean isUsable) {
		this.isUsable = isUsable;
	}
	
	public boolean isWalkable() {
		return isWalkable;
	}
	
	public void setWalkable(boolean isWalkable) {
		this.isWalkable = isWalkable;
	}
	
	public int[] getUseEffectsId() {
		return useEffectsId;
	}
	
	public void setUseEffectsId(int[] useEffectsId) {
		this.useEffectsId = useEffectsId;
	}
	
	public int[] getWalkEffectsId() {
		return walkEffectsId;
	}
	
	public void setWalkEffectsId(int[] walkEffectsId) {
		this.walkEffectsId = walkEffectsId;
	}	
}