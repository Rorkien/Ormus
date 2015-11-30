package com.ormus.server.service;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.ormus.server.entity.Level;
import com.ormus.server.entity.World;
import com.ormus.server.model.handlers.EnemyHandler;
import com.ormus.server.model.handlers.TileHandler;
import com.ormus.shared.entity.Enemy;
import com.ormus.shared.entity.Tile;

/**
 * Serviço responsável por carregar os arquivos de mundo
 * e níveis. Sendo assim, é imprescindível que o serviço
 * de carregamento de recursos tenha sido carregado
 * antes.
 */
public class WorldLoadingService extends AbstractService<World[]> {

	@Override
	public void execute() {
		Type integerMapType = new TypeToken<Map<Integer, Integer>>(){}.getType();
		Type integerMapListType = new TypeToken<Map<Integer, List<Integer>>>(){}.getType();
		
		//Lê o arquivo de especificação de mundos
		try {
			FileReader specificationReader = new FileReader(new File("assets/worlds.json"));
			Gson jsonDeserializer = new Gson();
			WorldSpecification[] worldSpecifications = jsonDeserializer.fromJson(new JsonReader(specificationReader), WorldSpecification[].class);
			World[] worlds = new World[worldSpecifications.length];
			
			//Para cada especificação, é preciso carregar o nível, os itens e pontos especiais
			for (int i = 0; i < worldSpecifications.length; i++) {
				WorldSpecification specification = worldSpecifications[i];
				int levelWidth, levelHeight;
				Tile[] levelTiles;
				Map<Integer, List<Integer>> items;
				Map<Integer, Integer> enemies;				
				List<Enemy> enemiesList = new ArrayList<Enemy>();
				
				//Cada nível é um arquivo PNG, onde cada pixel significa um bloco, definido pela cor dele.
				//Cada mundo pode ter seu nível diferente, e cada nível pode ter sua própria codificação.
				try {
					FileReader tileCodeReader = new FileReader(new File(specification.path + "/tilecodes.json"));

					Map<Integer, Integer> tileCodes = jsonDeserializer.fromJson(new JsonReader(tileCodeReader), integerMapType);

					//Lê a imagem e a transforma em um vetor de inteiros
					BufferedImage levelImage = ImageIO.read(new File(specification.path + "/map.png"));

					levelWidth = levelImage.getWidth();
					levelHeight = levelImage.getHeight();
					int[] levelData = new int[levelWidth * levelHeight];
					levelImage.getRGB(0, 0, levelWidth, levelHeight, levelData, 0, levelWidth);
					levelTiles = new Tile[levelData.length];

					//Converte as cores da imagem em referências a blocos
					for (int j = 0; j < levelData.length; j++) {
						int currentColor = levelData[j] & 0xFFFFFF;

						if (tileCodes.get(currentColor) == null) {
							System.out.printf("%d does not exist as a coded tile!\n", currentColor);
							tileCodes.put(currentColor, 0);
						}
						levelTiles[j] = TileHandler.getTile(tileCodes.get(currentColor));
					}
				} catch (IOException e) {
					System.out.printf("Error loading encoded tile data: %s\n", e.getMessage());
					continue;
				}
				
				//Lê o ponto inicial do mapa
				Point spawnLocation = new Point(specification.spawn % levelWidth, specification.spawn / levelWidth);
				
				try {
					//Lê os itens e pontos especiais do nível
					FileReader itemsReader = new FileReader(new File(specification.path + "/items.json"));
					items = jsonDeserializer.fromJson(new JsonReader(itemsReader), integerMapListType);
					
					//Lê os inimigos do nível
					FileReader enemiesReader = new FileReader(new File(specification.path + "/enemies.json"));
					enemies = jsonDeserializer.fromJson(new JsonReader(enemiesReader), integerMapType);
					
					for (Entry<Integer, Integer> entry : enemies.entrySet()) {
						Enemy e = EnemyHandler.getEnemy(entry.getValue());
						
						int positionX = entry.getKey() % levelWidth;
						int positionY = entry.getKey() / levelWidth;
						
						e.setPosition(new Point(positionX, positionY));
						enemiesList.add(e);
					}					
					
					//TODO: pontos especiais (teleporte, etc)
				} catch (IOException e) {
					System.out.printf("Error loading world item data: %s\n", e.getMessage());
					continue;
				}
				 
				//Cria o nível
				Level level = new Level(levelTiles, levelWidth, levelHeight, spawnLocation, items);
				
				//Cria o mundo com o nível criado
				worlds[i] = new World(specification.name, level, enemiesList);
			}
			
			//Retorna os mundos carregados.
			doCallback(worlds);
		} catch (IOException e) {
			System.out.printf("Error loading world specification file: %s\n", e.getMessage());
		}
	}
	
	//Classe anônima para uma estrutura de dados que contém a especificação de um único mundo.
	class WorldSpecification {
		String name;
		String path;
		int spawn;
	}
}
