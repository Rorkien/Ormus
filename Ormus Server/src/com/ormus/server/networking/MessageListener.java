package com.ormus.server.networking;

/**
 * Classes que implementam esta interface poder�o
 * ser avisadas quando uma mensagem for recebida.
 */
public interface MessageListener {
	public void onMessageReceived(Object object);
}
