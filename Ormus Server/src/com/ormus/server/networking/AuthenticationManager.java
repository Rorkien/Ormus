package com.ormus.server.networking;

import java.util.HashSet;
import java.util.Set;

import com.ormus.server.entity.World;
import com.ormus.server.gamestate.MOTD;
import com.ormus.server.gamestate.PlayerAdapter;
import com.ormus.server.gamestate.WorldManager;
import com.ormus.server.model.dao.PlayerDAO;
import com.ormus.shared.entity.Player;
import com.ormus.shared.gamestate.AuthenticationSignal;
import com.ormus.shared.gamestate.CreateSignal;
import com.ormus.shared.gamestate.Message;
import com.ormus.shared.networking.Connection;
import com.ormus.shared.networking.MessageListener;

/**
 * Classe responsável por reunir todas conexões realizadas ao
 * servidor, tornando-os em jogadores efetivos assim que uma
 * mensagem de autenticação é enviada.
 */
public class AuthenticationManager implements ConnectionListener {
	private WorldManager manager;
	private PlayerDAO playerDAO = new PlayerDAO();
	private Set<Connection> connections = new HashSet<Connection>();
	
	public AuthenticationManager(WorldManager manager) {
		this.manager = manager;
		
		for (World world : manager.getWorlds()) {
			if (world == null) continue;
			world.setAuthenticationManager(this);
		}
	}
	
	public void addConnection(Connection connection) {
		connections.add(connection);
		connection.addMessageListener(new MessageListener() {
			
			@Override
			public void onMessageReceived(Object object) {
				//Se a mensagem recebida for um objeto da classe AuthenticationSignal, verifica.
				//Senão, somente ignora.
				if (object instanceof CreateSignal) {
					CreateSignal signal = (CreateSignal) object;
					
					//Verifica se o jogador já existe.
					boolean playerExists = playerDAO.playerExists(signal.getName());
					
					if (signal.getName().length() > 6) {
						connection.send(new Message("A player name can't be more than 6 characters long."));
					} else {
						if (playerExists) {
							connection.send(new Message("A player with that name already exists."));
						} else {
							//Cria o jogador
							playerDAO.createPlayer(signal.getName(), signal.getPassword());
							Player player = playerDAO.getPlayer(signal.getName(), signal.getPassword());
							//Retira o jogador da fila de autenticação e o coloca no mundo desejado.
							if (manager.authenticate(player, connection, signal.getWorld())) {
								System.out.printf("%s created new character with username %s\n", connection, signal.getName());
								connection.removeMessageListener(this);
								connections.remove(connection);
							}
						}
					}

					
				} else if (object instanceof AuthenticationSignal) {
					AuthenticationSignal signal = (AuthenticationSignal) object;
					
					//Verifica se o jogador existe.
					Player player = playerDAO.getPlayer(signal.getName(), signal.getPassword());
					if (player != null) {
						//Verifica se o jogador já está conectado
						for (World world : manager.getWorlds()) {
							if (world == null) continue;
							for (PlayerAdapter playerAdapter : world.getPlayers()) {
								if (player.equals(playerAdapter.getPlayer())) {
									connection.send(new Message("You were already logged in."));
									playerAdapter.getConnection().send(new Message("Disconnecting you due to a new login"));
									playerAdapter.setShouldDisconnect(true);
								}
							}
						}
						
						//Retira o jogador da fila de autenticação e o coloca no mundo desejado.
						if (manager.authenticate(player, connection, signal.getWorld())) {
							System.out.printf("%s authenticated with username %s\n", connection, signal.getName());
							connection.removeMessageListener(this);
							connections.remove(connection);
						}
					} else {
						//Se usuário errou o usuário e/ou senha, o avisa.
						connection.send(new Message("Incorrect username/password."));
						System.out.printf("Authentication failure on %s with username %s\n", connection, signal.getName());
					}
				}
			}
		});
	}

	@Override
	public void onConnectionRequest(Connection connection) {
		//Adiciona a conexão à fila de autenticação
		addConnection(connection);
		
		//Manda a message-of-the-day para o usuário conectado
		for (String string : MOTD.getMOTD()) connection.send(new Message(string));	
		
		System.out.println(connection.toString() + " connected");
	}
}
