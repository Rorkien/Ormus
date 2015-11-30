package com.ormus.server;

import com.ormus.server.entity.World;
import com.ormus.server.gamestate.StatisticsManager;
import com.ormus.server.gamestate.WorldManager;
import com.ormus.server.model.dao.BaseDAO;
import com.ormus.server.model.handlers.EffectHandler;
import com.ormus.server.model.handlers.EnemyHandler;
import com.ormus.server.model.handlers.ItemHandler;
import com.ormus.server.model.handlers.TileHandler;
import com.ormus.server.networking.AuthenticationManager;
import com.ormus.server.networking.ConnectionListenerWorker;
import com.ormus.server.service.ResourceLoadingService;
import com.ormus.server.service.Service.Callback;
import com.ormus.server.service.WorldLoadingService;
import com.ormus.shared.entity.Effect;
import com.ormus.shared.entity.Enemy;
import com.ormus.shared.entity.Item;
import com.ormus.shared.entity.Tile;

public class Server {
	/*
	 * Estes são os parâmetros 
	 */
	public static final String DATABASE_PATH = "./ormus";
	public static final int DEFAULT_PORT = 8900;
	
	private ConnectionListenerWorker connectionListenerWorker;
	private AuthenticationManager authenticationManager;
	private WorldManager worldManager = new WorldManager();
	private static StatisticsManager statistics = new StatisticsManager();
	
	public Server() {		
		//Carrega a classe do banco de dados.
		System.out.print("Loading JDBC drivers for persistence... ");
		try {
			Class.forName("org.h2.Driver");
			System.out.println("Done.");
		} catch (ClassNotFoundException e) {
			System.out.printf(" Failed: %s", e.getMessage());
		}
		
		//Checa se o banco de dados existe
		BaseDAO baseDAO = new BaseDAO();
		if (!baseDAO.checkIfExists()) System.out.println("WARNING: The specified database doesn't exist!");
		
		//Registra e executa o serviço de carga de elementos estáticos, tais como itens, efeitos e criaturas.
		//Esse serviço não possui callback.
		ResourceLoadingService resourceLoader = new ResourceLoadingService();
		resourceLoader.registerResource(ItemHandler.getHandler(), Item[].class, "items.json");
		resourceLoader.registerResource(EffectHandler.getHandler(), Effect[].class, "effects.json");
		resourceLoader.registerResource(TileHandler.getHandler(), Tile[].class, "tiles.json");
		resourceLoader.registerResource(EnemyHandler.getHandler(), Enemy[].class, "enemies.json");		
		System.out.print("Loading resources... ");
		resourceLoader.execute();
		System.out.println("Done.");

		//Registra e executa o serviço de carga do nível de jogo, incluindo blocos, itens e criaturas.
		WorldLoadingService worldLoader = new WorldLoadingService();
		worldLoader.addCallback(new Callback<World[]>() {
			@Override
			public void onCallback(World[] callbackObject) {
				worldManager.offer(callbackObject);
				System.out.println("Done.");
			}
		});
		System.out.print("Loading world data... ");
		worldLoader.execute();
		
		//Escuta na porta padrão por conexões e configura o autenticador.
		connectionListenerWorker = new ConnectionListenerWorker(DEFAULT_PORT);
		authenticationManager = new AuthenticationManager(worldManager);
		connectionListenerWorker.addConnectionListener(authenticationManager);
		
		//Alimenta o gerenciador de estatísticas com os objetos necessários
		statistics.setWorldManager(worldManager);
		
		//Servidor inicializado e pronto para receber jogadores.
		System.out.printf("Loading complete. Now listening to port %d\n", connectionListenerWorker.getPort());
	}

	public static StatisticsManager getStatistics() {
		return statistics;
	}
	
	public static void main(String[] args) {		
		new Server();
	}
}
