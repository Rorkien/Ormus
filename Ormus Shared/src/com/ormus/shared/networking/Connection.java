package com.ormus.shared.networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class Connection {
	Set<MessageListener> listeners = new HashSet<MessageListener>();
	boolean connected = true;
	transient Socket socket;
	transient ObjectOutputStream outputStream;
	
	public void addMessageListener(MessageListener listener) {
		listeners.add(listener);
	}
	
	public void removeMessageListener(MessageListener listener) {
		listeners.remove(listener);
	}
	
	public void clearMessageListeners() {
		listeners.clear();
	}
	
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		
		BufferedOutputStream bufferedOutput = new BufferedOutputStream(socket.getOutputStream());
		
		outputStream = new ObjectOutputStream(bufferedOutput);
		new InputThread(socket.getInputStream());
	}
	
	public Connection(String host, int port) throws UnknownHostException, IOException {
			this(new Socket(InetAddress.getByName(host), port));
	}
	
	public boolean send(Object object) {
		try {			
			outputStream.writeObject(object);
			outputStream.flush();
			outputStream.reset();
			return true;
		} catch (SocketException e) {
			System.out.println(this + " forced a disconnection");
			close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Something bad happened!");
			close();
		}
		return false;
	}
	
	public void close() {
		try {
			connected = false;
			socket.close();
		} catch (IOException e) {
			System.out.println("Error closing socket: " + e.getMessage());
		}
	}
	
	public String toString() {
		return socket.getRemoteSocketAddress().toString();
	}
	
	class InputThread extends Thread implements Runnable {
		ObjectInputStream inputStream;
		
		public InputThread(InputStream inputStream) {
			try {
				Connection.this.outputStream.flush();
				this.inputStream = new ObjectInputStream(new BufferedInputStream(inputStream));
			} catch (IOException e) {
				System.out.println("Error creating input thread: " + e.getMessage());
			}
			start();
		}
		
		public void run() {
			try {
				while (connected) {
					try {
						Object object = inputStream.readObject();						
						for (MessageListener listener : listeners) listener.onMessageReceived(object);
					} catch (ClassNotFoundException e) {
						System.out.println("Object class not found: " + e.getMessage());
					}					
				}
			} catch (IOException e) {
				close();
			}
		}
	}
}
