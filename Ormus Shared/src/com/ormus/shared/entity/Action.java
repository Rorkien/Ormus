package com.ormus.shared.entity;

import java.io.Serializable;

public interface Action extends Serializable {
	public static final int CURRENT = -1;
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	
	public int getActionCooldownTicks();
}
