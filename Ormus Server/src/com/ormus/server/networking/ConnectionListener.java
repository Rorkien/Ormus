package com.ormus.server.networking;

import com.ormus.shared.networking.Connection;

/**
 * Classes que implementam esta interface poder�o
 * ser avisadas quando uma nova conex�o acontecer.
 */
public interface ConnectionListener {
	public void onConnectionRequest(Connection connection);
}
