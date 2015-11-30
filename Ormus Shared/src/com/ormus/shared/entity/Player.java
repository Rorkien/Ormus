package com.ormus.shared.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class Player extends Creature implements Serializable {	
	public static final int BASE_STRENGTH = 8;
	public static final int BASE_AGILITY = 8;
	public static final int BASE_DAMAGE = 5;
	public static final int BASE_HITPOINTS = 20;
	
	private static final long serialVersionUID = 1L;
	private int experience;
	private int[] equipment = new int[6]; //Cabeça, peito, pernas, mãos, pés, arma
	private transient Queue<Action> orders = new LinkedList<Action>();
	
	public static int getLevelFromExperience(int experience) {
		return (int) (Math.sqrt(experience + 400) - 20 + 1);
	}
	
	public static int getExperienceForLevel(int level) {
		return (int) (Math.pow(20 + (level - 1), 2) - 400);	
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public int[] getEquipment() {
		return equipment;
	}

	public void setEquipment(int[] equipment) {
		this.equipment = equipment;
	}

	public Queue<Action> getOrders() {
		return orders;
	}
	
	public void setOrders(Queue<Action> orders) {
		this.orders = orders;
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return getName().equals(((Player) obj).getName());
	}
}
