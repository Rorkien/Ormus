package com.ormus.server.gamestate;

import com.ormus.server.entity.World;
import com.ormus.shared.entity.Player;
import com.ormus.shared.gamestate.Message;
import com.ormus.shared.networking.Connection;

public class WorldManager {
	private World[] worlds;
	
	public void offer(World[] worlds) {
		this.worlds = worlds;
	}
	
	public World[] getWorlds() {
		return worlds;
	}
	
	public boolean authenticate(Player player, Connection connection, String world) {
		try {
			int worldId = Integer.valueOf(world.trim()) - 1; 
			if (worldId >= 0 && worldId < worlds.length) {
				if (worlds[worldId].canConnect()) {					
					PlayerAdapter playerAdapter = new PlayerAdapter(player, connection);
					worlds[worldId].addPlayer(playerAdapter);
					
					connection.send(new Message("Welcome back!"));
					return true;
				} else {
					connection.send(new Message("This world is closed."));
					return false;
				}
			}
		} catch (NumberFormatException e) {
		}
		connection.send(new Message("Invalid world."));
		return false;
	}
}
