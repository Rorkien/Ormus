package com.ormus.server.gamestate;

import com.ormus.server.entity.World;

public class StatisticsManager {
	WorldManager worldManager;
	
	public void setWorldManager(WorldManager worldManager) {
		this.worldManager = worldManager;
	}
	
	public int getOnline() {
		World[] worlds = worldManager.getWorlds();
		int amount = 0;
		
		for (int i = 0; i < worlds.length; i++) {
			if (worlds[i] != null) amount += worlds[i].getPlayers().size();
		}
		return amount;
	}

}
