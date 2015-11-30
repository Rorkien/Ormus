package com.ormus.server.model.handlers;

import com.ormus.shared.entity.Enemy;

/**
 * Classe responsável por armazenar os
 * objetos que definem Criaturas.
 */
public class EnemyHandler extends AbstractCachedHandler<Integer, Enemy> {
	private static EnemyHandler handler = new EnemyHandler();
	
	public static EnemyHandler getHandler() {
		return handler;
	}
	
	//Como cada referência a uma criatura no nível representa uma criatura diferente,
	//com seus próprios atributos, é necessário clonar o objeto existente no cache.
	public static Enemy getEnemy(Integer id) {
		Enemy original = handler.get(id);
		Enemy cloned = new Enemy();
		
		cloned.setId(original.getId());
		cloned.setBehaviorId(original.getBehaviorId());
		cloned.setName(original.getName());
		
		cloned.setHitpoints(original.getHitpoints());
		cloned.setMaxHitpoints(original.getMaxHitpoints());
		cloned.setDamage(original.getDamage());
		cloned.setArmor(original.getArmor());
		cloned.setStrength(original.getStrength());
		cloned.setAgility(original.getAgility());

		return cloned;
	}

	@Override
	public void offer(Enemy[] array) {
		for (Enemy enemy : array) {
			handler.add(enemy.getId(), enemy);		
		}
	}
}