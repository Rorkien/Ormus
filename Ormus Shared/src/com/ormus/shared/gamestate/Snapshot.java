package com.ormus.shared.gamestate;

import java.io.Serializable;

import com.ormus.shared.entity.Effect;
import com.ormus.shared.entity.Enemy;
import com.ormus.shared.entity.Item;
import com.ormus.shared.entity.Player;

public class Snapshot implements Serializable{
	private static final long serialVersionUID = 1L;
	private Player self;
	private Player[] other;
	private Enemy[] enemies;
	private Integer[] levelTiles;
	private Integer[] levelItems;
	private Item[] items;
	private Effect[] effects;
	
	public Snapshot(Player self, Player[] other, Enemy[] enemies, Integer[] levelTiles, Integer[] levelItems, Item[] items, Effect[] effects) {
		this.self = self;
		this.other = other;
		this.enemies = enemies;
		this.levelTiles = levelTiles;
		this.levelItems = levelItems;
		this.items = items;
		this.effects = effects;
	}
		
	public Player getSelf() {
		return self;
	}

	public Player[] getOther() {
		return other;
	}

	public Enemy[] getEnemies() {
		return enemies;
	}

	public Integer[] getLevelTiles() {
		return levelTiles;
	}

	public Integer[] getLevelItems() {
		return levelItems;
	}

	public Item[] getItems() {
		return items;
	}

	public Effect[] getEffects() {
		return effects;
	}
}
