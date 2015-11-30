package com.ormus.server.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata para implementa��o de servi�os.
 * 
 * Um servi�o possui o seguinte ciclo de execu��o:
 * 
 *  1. O servi�o � instanciado
 *  2. Objetos s�o adicionados como "escutadores" pelos
 *  m�todos addCallback() e removeCallback()
 *  2. O servi�o � executado pelo m�todo execute()
 *  3. O servi�o retorna um objeto para quem estiver
 *  registrado como um escutador pelo m�todo doCallback()
 *  
 *  A t�cnica de retornar um objeto ap�s a execu��o
 *  do servi�o se chama Callback, e serve primariamente
 *  para que os "escutadores" saibam quando o servi�o
 *  terminou sua execu��o
 *  
 *  Escutadores podem ser qualquer objeto, desde que
 *  implementem a interface Callback e seus m�todos.
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
