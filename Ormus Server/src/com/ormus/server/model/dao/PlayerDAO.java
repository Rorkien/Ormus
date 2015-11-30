package com.ormus.server.model.dao;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ormus.server.entity.World;
import com.ormus.shared.entity.Player;

public class PlayerDAO extends BaseDAO {
	
	public Player getPlayer(String name, String password) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement("SELECT * FROM players WHERE name = ? AND key = ?");
			statement.setString(1, name);
			statement.setString(2, password);

			
			rs = statement.executeQuery();
			if (rs.next()) {
				Player p = new Player();
				p.setName(rs.getString("name"));
				p.setExperience(rs.getInt("experience"));
				p.setHitpoints(rs.getInt("hitpoints"));
				
				
				//TODO: carregar o inventario equipment
				
				if (rs.getArray("inventory") != null) {
					Object[] inventoryIndex = (Object[]) rs.getArray("inventory").getArray();
					Integer[] inventoryItems = new Integer[inventoryIndex.length];
		
					for (int i = 0; i < inventoryItems.length; i++) {
						inventoryItems[i] = (Integer) inventoryIndex[i];
					}
					p.setInventoryAsArray(inventoryItems);
				}

				return p;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public boolean playerExists(String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement("SELECT * FROM players WHERE name = ?");
			statement.setString(1, name);
			
			rs = statement.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public Point getPositionFromPlayerWorld(Player player, String worldName) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement("SELECT * FROM players_worlds WHERE player_name = ? AND world = ?");
			statement.setString(1, player.getName());
			statement.setString(2, worldName);
			
			rs = statement.executeQuery();
			if (rs.next()) {
				Point p = new Point(rs.getInt("position_x"), rs.getInt("position_y"));
				return p;
			}
									
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void savePlayer(Player player) {
		Connection connection = null;
		try {
			connection = getConnection();
			savePlayer(connection, player);			
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void savePlayer(Connection connection, Player player) {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement("UPDATE players SET experience = ?, hitpoints = ?, inventory = ? WHERE name = ?");
			statement.setInt(1, player.getExperience());
			statement.setInt(2, player.getHitpoints());	
			statement.setObject(3, player.getInventoryAsArray());
			statement.setObject(4, player.getName());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void savePlayerWorldPosition(Player player, World world) {
		Connection connection = null;
		try {
			connection = getConnection();
			savePlayerWorldPosition(connection, player, world);			
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void savePlayerWorldPosition(Connection connection, Player player, World world) {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement("MERGE INTO players_worlds (player_name, world, position_x, position_y) KEY(player_name) VALUES(?, ?, ?, ?)");
			statement.setString(1, player.getName());
			statement.setString(2, world.getWorldName());
			statement.setInt(3, player.getPosition().x);
			statement.setInt(4, player.getPosition().y);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void createPlayer(String name, String password) {
		Connection connection = null;	
		PreparedStatement statement = null;
		try {
			connection = getConnection();
			statement = connection.prepareStatement("INSERT INTO players (key, name, hitpoints) VALUES (?, ?, ?);");
			statement.setString(1, password);
			statement.setString(2, name);
			statement.setInt(3, Player.BASE_HITPOINTS * 2);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
