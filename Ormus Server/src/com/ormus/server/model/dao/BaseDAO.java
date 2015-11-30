package com.ormus.server.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ormus.server.Server;

/**
 * Classe de acesso a dados que contém métodos básicos para
 * criar uma conexão com o banco de dados
 */
public class BaseDAO {
	/**
	 * Cria uma conexão com o banco de dados, que pode ser reutilizada.
	 */
	public Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:h2:" + Server.DATABASE_PATH);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Cria a conexão especificada.
	 */	
	public void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checa se o banco de dados especificado existe, segundo método do H2
	 */
	public boolean checkIfExists() {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) AS count FROM information_schema.tables WHERE table_name = 'PLAYERS'");

			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0) return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
}
