package com.ormus.ormusgame.networking;

import java.io.IOException;
import java.net.ConnectException;

import com.ormus.ormusgame.Application;
import com.ormus.ormusgame.utils.Localization;
import com.ormus.ormusgame.utils.Logger;
import com.ormus.ormusgame.utils.PreferencesManager;
import com.ormus.osl.Runtime;
import com.ormus.shared.gamestate.Message;
import com.ormus.shared.gamestate.Snapshot;
import com.ormus.shared.networking.Connection;
import com.ormus.shared.networking.MessageListener;

public class Client implements MessageListener {
	Snapshot lastSnapshot;
	Snapshot currentSnapshot;
	Connection connection;
	boolean isConnected = false;
	
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public Client() {
		try {
			connection = new Connection(PreferencesManager.getPreferences("network", "masterserver"), Integer.valueOf(PreferencesManager.getPreferences("network", "port")));
			connection.addMessageListener(this);
		} catch (ConnectException e) {
			Application.out.println(Localization.getString("networking.error.connecting"));
			Logger.log(Logger.ERROR, "Error while connecting to server: " + e.getMessage());
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "Error while connecting to server: " + e.getMessage());
		}

		Runtime.getRuntime().setClient(this);
	}
	
	public void send(Object message) {
		connection.send(message);
	}
	
	public Snapshot getCurrentSnapshot() {
		return currentSnapshot;
	}

	@Override
	public void onMessageReceived(Object object) {
		if (object instanceof Message) {
			Application.out.println(((Message) object).getContents());
		} else if (object instanceof Snapshot) {
			if (currentSnapshot != null) setConnected(true);
			lastSnapshot = currentSnapshot;
			currentSnapshot = (Snapshot) object;
			
			/*
			Runtime.getRuntime().setVariable("player.x", currentSnapshot.getPlayer().getPosition().x);
			if (lastSnapshot != null && lastSnapshot.getPlayer().getInventory() != null) {
				for (int i = 0; i < lastSnapshot.getPlayer().getInventory().size(); i++) {
					Runtime.getRuntime().unsetVariable("player.inventory." + (i + 1));
					Runtime.getRuntime().unsetVariable("player.inventory." + (i + 1) + ".amount");
				}
			}
			
			if (currentSnapshot.getPlayer().getInventory() != null) {
				Runtime.getRuntime().setVariable("player.inventory.size", currentSnapshot.getPlayer().getInventory().size());
				Iterator<Entry<Integer, Integer>> iterator = currentSnapshot.getPlayer().getInventory().entrySet().iterator();
				int i = 0;
				while (iterator.hasNext()) {
					Entry<Integer, Integer> entry = iterator.next();
					Runtime.getRuntime().setVariable("player.inventory." + (i + 1), entry.getKey());
					Runtime.getRuntime().setVariable("player.inventory." + (i + 1) + ".amount", entry.getValue());
					i++;
				}
			}
			*/
		} else if (object == null) {
			setConnected(false);
		}
	}

	
}
