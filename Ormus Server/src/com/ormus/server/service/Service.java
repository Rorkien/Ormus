package com.ormus.server.service;

/**
 * Interface para implementação de serviços e
 * callbacks
 * 
 * @see AbstractService
 */
public interface Service<T> {
	public void execute();
	public void addCallback(Callback<T> callback);
	public void removeCallback(Callback<T> callback);

	interface Callback<T> {
		public void onCallback(T callbackObject);
	}
}
