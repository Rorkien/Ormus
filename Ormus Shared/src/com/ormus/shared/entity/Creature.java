package com.ormus.shared.entity;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Creature implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Point position;
	private Map<Integer, Integer> inventory = new LinkedHashMap<Integer, Integer>();
	private Map<Integer, Integer> effects = new LinkedHashMap<Integer, Integer>();
	private int hitpoints;
	private int maxHitpoints;
	private int damage;
	private int armor;
	private int strength;
	private int agility;
	private transient long actionCooldown;
		
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		if (this.position == null) this.position = position;
		else this.position.setLocation(position);
	}

	public Map<Integer, Integer> getInventory() {
		return inventory;
	}

	public void setInventory(Map<Integer, Integer> inventory) {
		this.inventory = inventory;
	}

	public Map<Integer, Integer> getEffects() {
		return effects;
	}

	public void setEffects(Map<Integer, Integer> effects) {
		this.effects = effects;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	public int getMaxHitpoints() {
		return maxHitpoints;
	}

	public void setMaxHitpoints(int maxHitpoints) {
		this.maxHitpoints = maxHitpoints;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getAgility() {
		return agility;
	}

	public void setAgility(int agility) {
		this.agility = agility;
	}

	public long getActionCooldown() {
		return actionCooldown;
	}

	public void setActionCooldown(long actionCooldown) {
		this.actionCooldown = actionCooldown;
	}
	
	//Métodos de ajuda

	public Integer[] getInventoryAsArray() {
		Map<Integer, Integer> inventory = getInventory();
		List<Integer> inventoryItems = new ArrayList<Integer>();
		
		for (Integer item : inventory.keySet()) {
			for (int i = 0; i < inventory.get(item); i++) {
				inventoryItems.add(item);
			}
		}
		
		return inventoryItems.toArray(new Integer[inventoryItems.size()]);
	}
	
	public void setInventoryAsArray(Integer[] items) {
		Map<Integer, Integer> inventory = new HashMap<Integer, Integer>();
		
		for (Integer item : items) {
			inventory.put(item, (inventory.get(item) == null) ? 1 : inventory.get(item) + 1);
		}
		
		setInventory(inventory);
	}
	
	public void addItem(Integer item, int quantity) {
		boolean itemFound = false;
		for (Integer checkItem : getInventory().keySet()) {
			if (checkItem == item) {
				getInventory().put(checkItem, getInventory().get(checkItem) + quantity);
				itemFound = true;
				break;
			}
		}
		if (!itemFound) getInventory().put(item, quantity);
	}
	
	public void removeItem(Integer item) {
		if (getInventory().get(item) > 1) getInventory().put(item, getInventory().get(item) - 1);
		else getInventory().remove(item);
	}
	
	public void addEffect(Integer effect, Integer duration) {
		if (getEffects().get(effect) != null) getEffects().put(effect, getEffects().get(effect) + duration);
		else getEffects().put(effect, duration);
	}
}
