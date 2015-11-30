package com.ormus.server.model.handlers;

import com.ormus.shared.entity.Effect;

/**
 * Classe responsável por armazenar os
 * objetos que definem Efeitos.
 */
public class EffectHandler extends AbstractCachedHandler<Integer, Effect> {
	private static EffectHandler handler = new EffectHandler();
	
	public static EffectHandler getHandler() {
		return handler;
	}	
	
	public static Effect getEffect(Integer id) {
		return handler.get(id);
	}

	@Override
	public void offer(Effect[] array) {
		for (Effect effect : array) {
			handler.add(effect.getId(), effect);		
		}
	}
}
