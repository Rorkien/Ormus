package com.ormus.server.model.handlers;


/**
 * Interface para implementação de handlers.
 * 
 * Handlers são objetos de contenção de outros
 * objetos, e são utilizados para armazenar
 * itens, criaturas, efeitos, etc.
 * 
 * @see AbstractHandler
 */
public interface Handler<K, T> {
	public void offer(T[] array);
	public int getSize();
	public void add(K key, T value);
	public T get(K key);
}
