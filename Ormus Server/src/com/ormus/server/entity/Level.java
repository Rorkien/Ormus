package com.ormus.server.entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ormus.server.model.handlers.ItemHandler;
import com.ormus.shared.entity.Item;
import com.ormus.shared.entity.Tile;

/**
 * Classe que define um n�vel.
 * 
 * Um n�vel cont�m blocos e os itens existentes nele
 */
public class Level {
	private Tile[] tiles;
	private int width;
	private int height;
	private Map<Integer, List<Integer>> items = new HashMap<Integer, List<Integer>>();
	private Point spawnLocation;
	
	public Level(Tile[] tiles, int width, int height, Point spawnLocation, Map<Integer, List<Integer>> items) {
		this.tiles = tiles;
		this.width = width;
		this.height = height;
		this.items = items;
		this.spawnLocation = spawnLocation;
	}
	
	/**
	 * Retorna a largura, em blocos, do n�vel
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Retorna a altura, em blocos, do n�vel
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Retorna a posi��o inicial onde os jogadores ser�o colocados
	 */
	public Point getSpawnLocation() {
		return spawnLocation;
	}

	/**
	 * Checa se uma posi��o est� livre, ou seja,
	 * pode-se mover a ela
	 */
	public boolean isFree(int x, int y) {
		return tiles[x + (y * width)].isWalkable();
	}

	/**
	 * Checa se uma posi��o est� livre, ou seja,
	 * pode-se mover a ela
	 */
	public boolean isFree(Point point) {
		return isFree(point.x, point.y);
	}
	
	/**
	 * Adiciona um item a uma determinada posi��o
	 */
	public void addItem(int itemId, Point point) {
		List<Integer> itemsInPoint = items.get(point.x + (point.y * width));
		if (itemsInPoint == null) itemsInPoint = new ArrayList<Integer>();
		itemsInPoint.add(itemId);
		items.put(point.x + (point.y * width), itemsInPoint);
	}
	
	/**
	 * Retira um item de uma determinada posi��o
	 */
	public Item retrieveItem(Point point) {
		List<Integer> itemsInPoint = items.get(point.x + (point.y * width));
		if (itemsInPoint == null || itemsInPoint.size() <= 0) return null;
		else return ItemHandler.getItem(itemsInPoint.remove(itemsInPoint.size() - 1));
	}
	
	/**
	 * Retorna a lista de itens existentes em uma determinada posi��o
	 */
	public List<Integer> getItemsAtPosition(int x, int y) {
		return items.get(x + (y * width));
	}
	
	/**
	 * Retorna a lista de blocos existentes em uma determinada posi��o
	 */
	public Tile getTileAtPosition(int x, int y) {
		return tiles[x + (y * width)];
	}
}
