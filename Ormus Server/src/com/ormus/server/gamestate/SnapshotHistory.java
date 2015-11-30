package com.ormus.server.gamestate;

import java.util.HashSet;
import java.util.Set;

import com.ormus.shared.entity.Effect;
import com.ormus.shared.entity.Item;

public class SnapshotHistory {
	Set<Integer> sentItems = new HashSet<Integer>();
	Set<Integer> sentEffects = new HashSet<Integer>();
	
	public boolean alreadySentItem(Integer itemId) {
		return sentItems.contains(itemId);
	}
	
	public boolean alreadySentEffect(Integer effectId) {
		return sentItems.contains(effectId);
	}
	
	public void sync(Item[] items, Effect[] effects) {
		for (Item item : items) sentItems.add(item.getId());
		for (Effect effect : effects) sentEffects.add(effect.getId());
	}
}

