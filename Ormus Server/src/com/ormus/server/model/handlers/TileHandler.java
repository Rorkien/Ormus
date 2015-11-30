package com.ormus.server.model.handlers;

import com.ormus.shared.entity.Tile;

/**
 * Classe responsável por armazenar os
 * objetos que definem Blocos.
 */
public class TileHandler extends AbstractCachedHandler<Integer, Tile> {
	private static TileHandler handler = new TileHandler();
	
	public static TileHandler getHandler() {
		return handler;
	}
	
	//Como cada referência a um bloco representa um bloco diferente,
	//é necessário clonar o objeto existente no cache.
	public static Tile getTile(Integer id) {
		Tile original = handler.get(id);
		Tile cloned = new Tile();
		
		cloned.setId(original.getId());
		cloned.setName(original.getName());
		cloned.setDescription(original.getDescription());
		cloned.setSpriteId(original.getSpriteId());
		cloned.setUsable(original.isUsable());
		cloned.setWalkable(original.isWalkable());
		if (original.getUseEffectsId() != null) cloned.setUseEffectsId(original.getUseEffectsId().clone());
		if (original.getWalkEffectsId() != null) cloned.setWalkEffectsId(original.getWalkEffectsId().clone());
		
		return cloned;
	}

	@Override
	public void offer(Tile[] array) {
		for (Tile tile : array) {
			handler.add(tile.getId(), tile);		
		}
	}
}
