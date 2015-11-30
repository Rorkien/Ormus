package com.ormus.shared.entity.actions;

import com.ormus.shared.entity.Action;

public class MoveAction implements Action {
	int direction;
	
	public MoveAction(int direction) {
		this.direction = direction;
	}
	
	public int getDirection() {
		return direction;
	}

	@Override
	public int getActionCooldownTicks() {
		return 10;
	}
}
