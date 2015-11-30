package com.ormus.server.model.handlers;


/**
 * Interface para implementa��o de handlers.
 * 
 * Handlers s�o objetos de conten��o de outros
 * objetos, e s�o utilizados para armazenar
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
