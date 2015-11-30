package com.ormus.server.networking;

import com.ormus.shared.networking.Connection;

/**
 * Classes que implementam esta interface poderão
 * ser avisadas quando uma nova conexão acontecer.
 */
public interface ConnectionListener {
	public void onConnectionRequest(Connection connection);
}
