package com.ormus.server.gamestate;

import com.ormus.shared.entity.Action;
import com.ormus.shared.entity.Player;
import com.ormus.shared.gamestate.LogoutSignal;
import com.ormus.shared.gamestate.Snapshot;
import com.ormus.shared.gamestate.StopSignal;
import com.ormus.shared.networking.Connection;
import com.ormus.shared.networking.MessageListener;

public class PlayerAdapter implements MessageListener {
	private Player player;
	private Snapshot snapshot;
	private SnapshotHistory snapshotHistory = new SnapshotHistory();
	private Connection connection;
	private boolean shouldDisconnect;
	
	public PlayerAdapter(Player player, Connection connection) {
		this.player = player;
		this.connection = connection;
		connection.addMessageListener(this);
	}
	
	public String getPlayerName() {
		return player.getName();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Snapshot getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(Snapshot snapshot) {
		this.snapshot = snapshot;
	}

	public SnapshotHistory getSnapshotHistory() {
		return snapshotHistory;
	}

	public void setSnapshotHistory(SnapshotHistory snapshotHistory) {
		this.snapshotHistory = snapshotHistory;
	}

	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean shouldDisconnect() {
		return shouldDisconnect;
	}

	public void setShouldDisconnect(boolean shouldDisconnect) {
		this.shouldDisconnect = shouldDisconnect;
	}

	@Override
	public void onMessageReceived(Object object) {
		if (object instanceof Action) {
			player.getOrders().offer((Action) object);
		} else if (object instanceof StopSignal) {
			player.getOrders().clear();
		} else if (object instanceof LogoutSignal) {
			System.out.println(connection + " requested a disconnection");
			setShouldDisconnect(true);
		}
	}	
	
}
