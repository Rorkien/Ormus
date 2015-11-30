package com.ormus.shared.entity.actions;

import com.ormus.shared.entity.Action;

public class UseAction implements Action {
	int itemId;
	
	public UseAction(int itemId) {
		this.itemId = itemId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	@Override
	public int getActionCooldownTicks() {
		return 100;
	}

}
