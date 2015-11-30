package com.ormus.shared.entity.actions;

import com.ormus.shared.entity.Action;

public class DropAction implements Action {
	int itemId;
	
	public DropAction(int itemId) {
		this.itemId = itemId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	@Override
	public int getActionCooldownTicks() {
		return 10;
	}

}
