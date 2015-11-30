package com.ormus.server.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata para implementação de serviços.
 * 
 * Um serviço possui o seguinte ciclo de execução:
 * 
 *  1. O serviço é instanciado
 *  2. Objetos são adicionados como "escutadores" pelos
 *  métodos addCallback() e removeCallback()
 *  2. O serviço é executado pelo método execute()
 *  3. O serviço retorna um objeto para quem estiver
 *  registrado como um escutador pelo método doCallback()
 *  
 *  A técnica de retornar um objeto após a execução
 *  do serviço se chama Callback, e serve primariamente
 *  para que os "escutadores" saibam quando o serviço
 *  terminou sua execução
 *  
 *  Escutadores podem ser qualquer objeto, desde que
 *  implementem a interface Callback e seus métodos.
 */
public abstract class AbstractService<T> implements Service<T> {
	private List<Callback<T>> callbackListeners = new ArrayList<Callback<T>>();

	@Override
	public void addCallback(Callback<T> callback) {
		callbackListeners.add(callback);
	}

	@Override
	public void removeCallback(Callback<T> callback) {		
		callbackListeners.remove(callback);
	}

	public void doCallback(T callbackObject) {
		for (Callback<T> listener : callbackListeners) {
			listener.onCallback(callbackObject);
		}
	}
}
