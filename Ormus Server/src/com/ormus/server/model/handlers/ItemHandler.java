package com.ormus.server.model.handlers;

import com.ormus.shared.entity.Item;

/**
 * Classe responsável por armazenar os
 * objetos que definem Itens.
 */
public class ItemHandler extends AbstractCachedHandler<Integer, Item> {
	private static ItemHandler handler = new ItemHandler();
	
	private ItemHandler() {
	}
	
	public static ItemHandler getHandler() {
		return handler;
	}	
	
	public static Item getItem(Integer id) {
		return handler.get(id);
	}
	
	public static Item getRandomItem() {
		return handler.getRandomValue();
	}

	@Override
	public void offer(Item[] array) {
		for (Item item : array) {
			handler.add(item.getId(), item);		
		}
	}
}
