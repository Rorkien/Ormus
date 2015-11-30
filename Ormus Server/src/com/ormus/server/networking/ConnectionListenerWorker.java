package com.ormus.server.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import com.ormus.shared.networking.Connection;

/**
 * Thread que escuta ativamente por novas conex�es.
 * Quando uma nova conex�o � recebida, ela � adicionada
 * ao gerenciador de autentica��o
 */
public class ConnectionListenerWorker extends Thread {
	private Set<ConnectionListener> listeners = new HashSet<ConnectionListener>();
	private ServerSocket serverSocket;
	private boolean listening = true;
	private int port;
	
	public void addConnectionListener(ConnectionListener listener) {
		listeners.add(listener);
	}
	
	public void removeConnectionListener(ConnectionListener listener) {
		listeners.remove(listener);
	}
	
	public ConnectionListenerWorker(int port) {
		this.port = port;
		setName("Listener Thread @ port " + port);
		start();
	}
	
	public int getPort() {
		return port;
	}
	
	public void close() throws IOException {
		listening = false;
		serverSocket.close();
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);

			while (listening) {
				//Escuta ativamente por conex�es
				Socket socket = serverSocket.accept();
				Connection connection = new Connection(socket);

				//Notifica todos os listeners que uma nova conex�o ocorreu
				for (ConnectionListener listener : listeners) listener.onConnectionRequest(connection);
			}
		} catch (IOException e) {
			if (listening) e.printStackTrace();
		}
	}	
}
