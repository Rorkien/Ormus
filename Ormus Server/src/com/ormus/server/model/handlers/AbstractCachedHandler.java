package com.ormus.server.model.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Este tipo de implementação de Handler
 * foca em armazenar objetos com cache.
 * 
 * Se um determinado objeto não existe,
 * ele é carregado e adicionado ao cache.
 * Do contrário, o objeto é retornado
 * diretamente do cache.
 */
public abstract class AbstractCachedHandler<K, T> implements Handler<K, T> {
	private Map<K, T> cache = new HashMap<K, T>();
	private Random random = new Random();
	
	@Override
	public int getSize() {
		return cache.size();
	}
	
	public T getRandomValue() {
		List<K> keySetList = new ArrayList<K>(cache.keySet());
		int randomKey = random.nextInt(getSize());
		return cache.get(keySetList.get(randomKey));
	}
	
	@Override
	public void add(K key, T value) {
		cache.put(key, value);
	}
	
	@Override
	public T get(K key) {
		return cache.get(key);
	}
}
