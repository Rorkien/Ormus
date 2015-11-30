package com.ormus.shared.entity.actions;

import com.ormus.shared.entity.Action;

public class AttackAction implements Action {
	
	public AttackAction() {
	}

	@Override
	public int getActionCooldownTicks() {
		return 300;
	}

}
