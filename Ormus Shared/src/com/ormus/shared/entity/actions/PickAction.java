package com.ormus.shared.entity.actions;

import com.ormus.shared.entity.Action;

public class PickAction implements Action {
	int direction;
	
	public PickAction(int direction) {
		this.direction = direction;
	}
	
	public int getDirection() {
		return direction;
	}
	
	@Override
	public int getActionCooldownTicks() {
		return 20;
	}

}
