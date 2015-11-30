package com.ormus.server.entity;

import java.awt.Point;
import java.awt.Rectangle;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.ormus.server.gamestate.PlayerAdapter;
import com.ormus.server.gamestate.SnapshotHistory;
import com.ormus.server.model.dao.PlayerDAO;
import com.ormus.server.model.handlers.EffectHandler;
import com.ormus.server.model.handlers.ItemHandler;
import com.ormus.server.networking.AuthenticationManager;
import com.ormus.shared.entity.Action;
import com.ormus.shared.entity.Creature;
import com.ormus.shared.entity.Effect;
import com.ormus.shared.entity.Enemy;
import com.ormus.shared.entity.Item;
import com.ormus.shared.entity.Player;
import com.ormus.shared.entity.actions.AttackAction;
import com.ormus.shared.entity.actions.DropAction;
import com.ormus.shared.entity.actions.EquipAction;
import com.ormus.shared.entity.actions.MoveAction;
import com.ormus.shared.entity.actions.PickAction;
import com.ormus.shared.entity.actions.UseAction;
import com.ormus.shared.gamestate.Message;
import com.ormus.shared.gamestate.Snapshot;

/**
 * Classe responsável pelo gerenciamento do ambiente de jogo. Ou seja,
 * contém todos os objetos disponíveis para interação e o próprio mundo
 * em si.
 * 
 * Um objeto World é uma instância ativa, e sua função é gerenciar os
 * jogadores e suas ações, tomando as decisões necessárias em cada caso. 
 */
public class World extends Thread {
	private long tics = 0;
	private String worldName;
	private boolean running = true;
	private boolean connectable = true;
	private boolean serverSave = false;
	private Level level;
	private AuthenticationManager authenticationManager;
	private List<PlayerAdapter> players = new ArrayList<PlayerAdapter>();
	private List<Enemy> enemies = new ArrayList<Enemy>();
	private PlayerDAO playerDAO = new PlayerDAO();
	private Connection connection = null;
	private Random random = new Random();
	
	public World(String worldName, Level level, List<Enemy> enemies) {
		this.worldName = worldName;
		this.level = level;
		this.enemies = enemies;
		start();
	}
	
	public boolean canConnect() {
		return running & connectable;
	}
	
	public void addPlayer(PlayerAdapter player) {
		synchronized (players) {
			Point p = playerDAO.getPositionFromPlayerWorld(player.getPlayer(), worldName);
			if (p == null) p = level.getSpawnLocation();
			
			player.getPlayer().setPosition(p);			
			players.add(player);
		}
	}
	
	public void removePlayer(PlayerAdapter player) {
		synchronized (players) {
			playerDAO.savePlayer(player.getPlayer());
			playerDAO.savePlayerWorldPosition(player.getPlayer(), this);
			players.remove(player);			
		}
	}
	
	public List<PlayerAdapter> getPlayers() {
		return players;
	}
	
	public String getWorldName() {
		return worldName;
	}
	
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void run() {
		while (running) {
			update();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.printf("Something went wrong with world %s: %s", worldName, e.getMessage());
				continue;
			}
		}	
	}

	public void update() {
		//long start = System.nanoTime();
		tics++;
		
		//Salva o mundo a cada 1 minuto
		if (tics % 6000 == 0) {
			serverSave = true;
			System.out.println("Server save will happen on this tic");
		}

		//Para cada criatura no mundo, executa suas ações
		synchronized (enemies) {
			Iterator<Enemy> enemyIterator = enemies.iterator();
			
			while (enemyIterator.hasNext()) {
				Enemy currentEnemy = enemyIterator.next();

				//Se a criatura está impossibilitada de tomar uma ação, adia a próxima ação dela
				if (currentEnemy.getActionCooldown() < tics) {
					if (currentEnemy.getBehaviorId() == 1) {
						//Comportamento passivo
						Point position = currentEnemy.getPosition();
						Point newPosition;
						
						//Move aleatoriamente
						if (random.nextBoolean()) {
							if (random.nextBoolean()) newPosition = new Point(position.x, position.y + 1);
							else newPosition = new Point(position.x, position.y - 1);							
						} else {
							if (random.nextBoolean()) newPosition = new Point(position.x + 1, position.y);
							else newPosition = new Point(position.x - 1, position.y);	
						}
						
						if (level.isFree(newPosition) && isFree(newPosition, Creature.class)) {
							currentEnemy.getPosition().setLocation(newPosition);
							currentEnemy.setActionCooldown(tics + 100);
						}
					} else if (currentEnemy.getBehaviorId() == 2) {
						//Comportamento agressivo
						Point position = currentEnemy.getPosition();
						
						Point playerNorth = new Point(position.x, position.y - 1);
						Point playerSouth = new Point(position.x, position.y + 1);
						Point playerWest = new Point(position.x - 1, position.y);
						Point playerEast = new Point(position.x + 1, position.y);
						
						List<PlayerAdapter> playersInRange = new ArrayList<PlayerAdapter>();						
						for (PlayerAdapter playerAdapter : players) {
							Player player = playerAdapter.getPlayer();
							
							if (player.getPosition().equals(playerNorth) || player.getPosition().equals(playerSouth) || player.getPosition().equals(playerWest) || player.getPosition().equals(playerEast)) {
								playersInRange.add(playerAdapter);
							}
						}
						
						if (playersInRange.size() > 0) {
							//Se tem um ou mais jogadores adjacentes, ataca um
							PlayerAdapter selectedPlayer = playersInRange.get(random.nextInt(playersInRange.size()));
							damagePlayer(currentEnemy, selectedPlayer);
							currentEnemy.setActionCooldown(tics + 300);
						} else {
							//Se nao tem um jogador adjacente, move aleatoriamente
							Point newPosition;
							
							if (random.nextBoolean()) {
								if (random.nextBoolean()) newPosition = new Point(position.x, position.y + 1);
								else newPosition = new Point(position.x, position.y - 1);							
							} else {
								if (random.nextBoolean()) newPosition = new Point(position.x + 1, position.y);
								else newPosition = new Point(position.x - 1, position.y);	
							}
							
							if (level.isFree(newPosition) && isFree(newPosition, Creature.class)) {
								currentEnemy.getPosition().setLocation(newPosition);
								currentEnemy.setActionCooldown(tics + 100);
							}
						}
					}
				}
			}
		}
		
		//Para cada jogador vinculado a este objeto, executa as ações enfileiradas deste,
		//gerando e enviando um Snapshot que contém as informações do ambiente ao redor deste.
		synchronized (players) {
			
			Iterator<PlayerAdapter> playerIterator = players.iterator();
			while (playerIterator.hasNext()) {
				PlayerAdapter playerAdapter = playerIterator.next();
				Player player = playerAdapter.getPlayer();
				
				if (playerAdapter.shouldDisconnect()) {					
					//Avisa ao jogador que ele foi desconectado, enviando um snapshot nulo
					playerAdapter.getConnection().send(null);
					
					//Devolve a conexão ao gerenciador de autenticação para caso o jogador
					//querer autenticar novamente sem precisar fechar o jogo
					if (authenticationManager != null) {
						playerAdapter.getConnection().clearMessageListeners();
						authenticationManager.addConnection(playerAdapter.getConnection());
					}
					
					//Remove o jogador do jogo
					playerDAO.savePlayer(player);
					playerDAO.savePlayerWorldPosition(player, this);
					playerIterator.remove();
					
					//Continua a execução para o próximo jogador
					continue;
				}
				
				int playerLevel = Player.getLevelFromExperience(player.getExperience());
				
				//Recalcula os atributos
				int playerDamage = Player.BASE_DAMAGE;
				int playerArmor = 0;
				int playerStrength = Player.BASE_STRENGTH;
				int playerAgility = Player.BASE_AGILITY;
				int playerMaxHitpoints = Player.BASE_HITPOINTS;
				
				playerDamage += playerLevel;
				playerArmor += playerLevel;
				playerStrength += playerLevel;
				playerAgility += playerLevel;
				playerMaxHitpoints += (playerLevel * 5);

				//TODO: atributos originados de força/agilidade
				
				//Recalcula os equipamentos
				for (int itemId : player.getEquipment()) {
					Item item = ItemHandler.getItem(itemId);
					
					if (item != null) {
						playerDamage += item.getDamage();
						playerArmor += item.getArmor();
						playerStrength += item.getStrength();
						playerAgility += item.getAgility();
						playerMaxHitpoints += item.getMaxHitpoints();	
					}
				}
				
				//Recalcula os efeitos ativos
				for (int effectId : player.getEffects().keySet()) {
					Effect effect = EffectHandler.getEffect(effectId);
					
					if (effect != null) {
						playerDamage += effect.getDamage();
						playerArmor += effect.getArmor();
						playerStrength += effect.getStrength();
						playerAgility += effect.getAgility();
						playerMaxHitpoints += effect.getMaxHitpoints();
					}
				}
				
				player.setDamage(playerDamage);
				player.setArmor(playerArmor);
				player.setStrength(playerStrength);
				player.setAgility(playerAgility);
				player.setMaxHitpoints(playerMaxHitpoints);
				
				//Checa os pontos de vida
				if (player.getHitpoints() > player.getMaxHitpoints()) player.setHitpoints(player.getMaxHitpoints());
				else if (player.getHitpoints() <= 0) {
					playerAdapter.getConnection().send(new Message("You died!"));
					player.setHitpoints(player.getMaxHitpoints());
					player.getPosition().setLocation(level.getSpawnLocation());
				}

				//Se o jogador está impossibilitado de tomar uma ação, adia a próxima ação deste jogador
				if (player.getActionCooldown() < tics) {
					Action action = player.getOrders().poll();
					boolean actionSucceeded = false;
					
					if (action != null) {
						if (action instanceof MoveAction) {
							//Se é uma ordem de movimento
							MoveAction moveAction = (MoveAction) action;
							Point newPosition = player.getPosition().getLocation();

							if (moveAction.getDirection() == Action.NORTH) newPosition.translate(0, -1);
							else if (moveAction.getDirection() == Action.SOUTH) newPosition.translate(0, 1);
							else if (moveAction.getDirection() == Action.WEST) newPosition.translate(-1, 0);
							else if (moveAction.getDirection() == Action.EAST)  newPosition.translate(1, 0);

							//Move o jogador para a nova posição
							if (level.isFree(newPosition) && isFree(newPosition, Player.class)) {
								player.getPosition().setLocation(newPosition);
								actionSucceeded = true;
							}
							else {
								playerAdapter.getConnection().send(new Message("Can't move."));
							}
						} else if (action instanceof PickAction) {
							//Se é uma ordem de pegar algum item
							PickAction pickAction = (PickAction) action;
							Point itemPosition = player.getPosition().getLocation();

							if (pickAction.getDirection() == Action.NORTH) itemPosition.translate(0, -1);
							else if (pickAction.getDirection() == Action.SOUTH) itemPosition.translate(0, 1);
							else if (pickAction.getDirection() == Action.WEST) itemPosition.translate(-1, 0);
							else if (pickAction.getDirection() == Action.EAST) itemPosition.translate(1, 0);

							//Retira o item do mapa e adiciona ao inventário do jogador
							Item item;
							if ((item = level.retrieveItem(itemPosition)) != null) {
								player.addItem(item.getId(), 1);
								playerAdapter.getConnection().send(new Message(String.format("%s (Id: %s) added to your inventory.", item.getName(), item.getId())));
								actionSucceeded = true;
							} else {
								playerAdapter.getConnection().send(new Message("There's nothing to pick."));
							}
						} else if (action instanceof UseAction) {
							//Se é uma ordem de usar algum item
							UseAction useAction = (UseAction) action;
							Integer itemId = useAction.getItemId();
							Item item = ItemHandler.getItem(useAction.getItemId());

							if (player.getInventory().get(itemId) != null && player.getInventory().get(itemId) > 0) {
								if (item.isConsumable()) {
									player.removeItem(itemId);
									int[] effects = item.getEffectsId();
									for (int effectId : effects) {
										Effect effect = EffectHandler.getEffect(effectId);
										player.addEffect(effectId, effect.getDuration());

										if (effect.getDuration() <= 1) applyEffect(playerAdapter, effect);
										else playerAdapter.getConnection().send(new Message(String.format("You are now under effects of %s: %s", effect.getName(), effect.getDescription())));
										actionSucceeded = true;
									}
								} else {
									playerAdapter.getConnection().send(new Message(String.format("%s isn't usable.", item.getName())));
								}
							}
						} else if (action instanceof EquipAction) {
							//Se é uma ordem de equipar algum item
							EquipAction equipAction = (EquipAction) action;
							Item item = ItemHandler.getItem(equipAction.getItemId());
							
							//Checa se é um item equipável
							if (item != null) {
								if (item.getSlot() > 0) {							
									//Remove o item atualmente equipado e coloca no inventário do jogador
									Item currentEquippedItem = ItemHandler.getItem(player.getEquipment()[item.getSlot()]);
									if (currentEquippedItem != null) player.addItem(currentEquippedItem.getId(), 1);

									player.getEquipment()[item.getSlot()] = item.getId();
									playerAdapter.getConnection().send(new Message(String.format("%s equipped.", item.getName())));
									actionSucceeded = true;
								} else {
									playerAdapter.getConnection().send(new Message(String.format("%s isn't equippable.", item.getName())));
								}
							}
						} else if (action instanceof AttackAction) {
							
							Point position = player.getPosition();
							
							Point enemyNorth = new Point(position.x, position.y - 1);
							Point enemySouth = new Point(position.x, position.y + 1);
							Point enemyWest = new Point(position.x - 1, position.y);
							Point enemyEast = new Point(position.x + 1, position.y);
							
							Enemy target = null;
							for (Enemy enemy : enemies) {
								if (enemy.getPosition().equals(enemyNorth) || enemy.getPosition().equals(enemySouth) || enemy.getPosition().equals(enemyWest) || enemy.getPosition().equals(enemyEast)) {
									if (target == null || enemy.getHitpoints() < target.getHitpoints()) target = enemy;
								}
							}
							
							if (target != null) {
								damageEnemy(playerAdapter, target);
								//Se a criatura está morta
								if (target.getHitpoints() <= 0) {
									//Remove ela do jogo
									enemies.remove(target);
									
									//Joga de zero a três itens no chão
									int itemAmount = (int) (random.nextInt(4));
									StringBuilder message = new StringBuilder();
									for (int i = 0; i < itemAmount; i++) {
										Item droppedItem = ItemHandler.getRandomItem();		
										level.addItem(droppedItem.getId(), target.getPosition());
										message.append(droppedItem.getName() + "(Id: " + droppedItem.getId() + "), ");
									}
									if (message.toString().length() <= 0) message.append("nothing, ");
									
									//Dá experiência ao jogador
									player.setExperience(player.getExperience() + target.getMaxHitpoints());
									int neededExperience = Player.getExperienceForLevel(playerLevel + 1) - player.getExperience();
									
									//Se o jogador passou de nível
									if (neededExperience <= 0) {
										playerLevel++;
										neededExperience = Player.getExperienceForLevel(playerLevel + 1) - player.getExperience();
										playerAdapter.getConnection().send(new Message(String.format("Congratulations! You are now on level %d. You gain 1 Damage, 1 Strength, 1 Agility and 5 Hitpoints.", playerLevel)));	
									}

									playerAdapter.getConnection().send(new Message(String.format("%s died and dropped: %s. You gained %d experience (%d needed for level %d).", target.getName(), message.toString().substring(0, message.length() - 2), target.getMaxHitpoints(), neededExperience, playerLevel + 1)));
									
								}
								actionSucceeded = true;
							}
						} else if (action instanceof DropAction) {
							//Se é uma ordem de jogar algum item
							DropAction dropAction = (DropAction) action;
							Integer droppedItemId = dropAction.getItemId();
							Item droppedItem = ItemHandler.getItem(dropAction.getItemId());
							
							//Checa se o jogador possui o item
							if (player.getInventory().get(droppedItemId) != null && player.getInventory().get(droppedItemId) > 0) {
								player.removeItem(droppedItemId);
								level.addItem(droppedItem.getId(), player.getPosition());
								playerAdapter.getConnection().send(new Message(String.format("%s dropped.", droppedItem.getName())));
								actionSucceeded = true;
							}
						}

						//Adiciona o tempo de espera para a ação recém-tomada
						if (actionSucceeded) player.setActionCooldown(tics + action.getActionCooldownTicks());
					}
				}

				//Aplica os efeitos existentes no jogador uma vez a cada 100 tics
				if (tics % 100 == 0) {
					Iterator<Integer> iterator = player.getEffects().keySet().iterator();
					while (iterator.hasNext()) applyEffect(playerAdapter, EffectHandler.getEffect(iterator.next()));					
				}
				
				//Gera um Snapshot a partir da posição do jogador, contendo o mundo, itens e outros jogadores
				
				/* antigo
				Snapshot playerSnapshot = new Snapshot();
				playerSnapshot.setPlayer(player);

				for (int y = player.getPosition().y - 4; y <= player.getPosition().y + 4; y++) {
					for (int x = player.getPosition().x - 5; x <= player.getPosition().x + 5; x++) {
						playerSnapshot.getTiles().add(level.getTileAtPosition(x, y));

						if (level.getItemsAtPosition(x, y) == null || level.getItemsAtPosition(x, y).size() <= 0) playerSnapshot.getItems().add(null);
						else {
							List<Integer> itemsIdAtPosition = level.getItemsAtPosition(x, y);
							List<Item> itemsAtPosition = new ArrayList<Item>();
							for (Integer itemId : itemsIdAtPosition) {
								itemsAtPosition.add(ItemHandler.getItem(itemId));
							}
							playerSnapshot.getItems().add(itemsAtPosition.get(itemsAtPosition.size() - 1));
						}
					}
				}

				Rectangle snapshotBox = new Rectangle(player.getPosition().x - 5, player.getPosition().y - 4, 11, 9);		
				for (PlayerAdapter referencedPlayerAdapter : players) {
					if (snapshotBox.contains(player.getPosition())) playerSnapshot.getPlayers().add(referencedPlayerAdapter.getPlayer());
				}

				playerAdapter.setSnapshot(playerSnapshot);
				*/
				
				//-----
				
				Rectangle snapshotDelimiter = new Rectangle(player.getPosition().x - 5, player.getPosition().y - 4, 11, 9);
				SnapshotHistory history = playerAdapter.getSnapshotHistory();
				Player self = player;
				Set<Player> other = new HashSet<Player>();
				Set<Enemy> enemies = new HashSet<Enemy>();				
				Integer[] levelTiles = new Integer[snapshotDelimiter.width * snapshotDelimiter.height];
				Integer[] levelItems = new Integer[snapshotDelimiter.width * snapshotDelimiter.height];
				Set<Item> items = new HashSet<Item>();
				Set<Effect> effects = new HashSet<Effect>();
				
				for (PlayerAdapter otherPlayerAdapter : players) {
					if (otherPlayerAdapter.getPlayer() != self && snapshotDelimiter.contains(player.getPosition())) other.add(otherPlayerAdapter.getPlayer());
				}
				
				for (Enemy enemy : this.enemies) {
					if (snapshotDelimiter.contains(enemy.getPosition())) {
						enemies.add(enemy);
					}
				}

				{
					int i = 0;
					for (int y = snapshotDelimiter.y; y < snapshotDelimiter.y + snapshotDelimiter.height; y++) {
						for (int x = snapshotDelimiter.x; x < snapshotDelimiter.x + snapshotDelimiter.width; x++) {
							levelTiles[i] = level.getTileAtPosition(x, y).getId();

							if (level.getItemsAtPosition(x, y) == null || level.getItemsAtPosition(x, y).size() <= 0) levelItems[i] = -1;
							else {
								List<Integer> itemsAtPosition = level.getItemsAtPosition(x, y);
								levelItems[i] = itemsAtPosition.get(itemsAtPosition.size() - 1);
							}

							i++;
						}
					}
				}

				//Adiciona os items que serão informados ao jogador destino
				//Itens do inventário do jogador
				for (Integer itemId : self.getInventoryAsArray()) {
					if (!history.alreadySentItem(itemId)) items.add(ItemHandler.getItem(itemId));
				}

				//Itens equipados pelo jogador
				for (Integer itemId : self.getEquipment()) {
					Item item  = ItemHandler.getItem(itemId);
					if (item != null && !history.alreadySentItem(itemId)) items.add(item);
				}

				//Itens no nível
				for (int i = 0; i < levelItems.length; i++) {
					Item item  = ItemHandler.getItem(levelItems[i]);
					if (item != null && !history.alreadySentItem(item.getId())) items.add(item);
				}

				//Efeitos existentes no jogador
				for (Entry<Integer, Integer> entry : self.getEffects().entrySet()) {
					if (!history.alreadySentEffect(entry.getKey())) effects.add(EffectHandler.getEffect(entry.getKey()));
				}

				//Efeitos de origem de itens
				for (Item item : items) {
					if (item.getEffectsId() != null) {
						for (int effectId : item.getEffectsId()) {
							if (!history.alreadySentEffect(effectId)) effects.add(EffectHandler.getEffect(effectId));				
						}
					}
				}
				
				Snapshot snapshot = new Snapshot(self, other.toArray(new Player[other.size()]), enemies.toArray(new Enemy[enemies.size()]), levelTiles, levelItems, items.toArray(new Item[items.size()]), effects.toArray(new Effect[effects.size()]));
				playerAdapter.setSnapshot(snapshot);

				//Salva o jogador, se for o momento
				if (serverSave) {
					if (connection == null) connection = playerDAO.getConnection();
					playerDAO.savePlayer(connection, player);
					playerDAO.savePlayerWorldPosition(connection, player, this);
				}
			}
			
			//Se os jogadores foram salvos, fecha a conexão com o banco
			if (serverSave && connection != null) {
				serverSave = false;
				playerDAO.closeConnection(connection);
				connection = null;
			}

			//Dissemina os Snapshots gerados aos jogadores
			Iterator<PlayerAdapter> snapshotIterator = players.iterator();
			while (snapshotIterator.hasNext()){
				PlayerAdapter player = snapshotIterator.next();

				if (player.getSnapshot() != null) {
					if (!player.getConnection().send(player.getSnapshot())) {
						//Se o jogador não existe mais, o salva e o retira do mundo.
						playerDAO.savePlayer(player.getPlayer());
						playerDAO.savePlayerWorldPosition(player.getPlayer(), this);
						snapshotIterator.remove();
					}
					player.getSnapshotHistory().sync(player.getSnapshot().getItems(), player.getSnapshot().getEffects());
				}
			}
		}
		
		//System.out.println((System.nanoTime() - start) / (1000D * 1000));
	}

	public boolean isFree(Point position, Class<?> ignoredClass) {
		List<Creature> creatures = new ArrayList<Creature>();
		
		for (PlayerAdapter player : players) creatures.add(player.getPlayer());
		creatures.addAll(enemies);

		for (Creature creature : creatures) {
			if (creature.getPosition().equals(position)) {
				if (creature.getClass() != ignoredClass) return false;				
			}
		}
		return true;
	}
	
	public void applyEffect(PlayerAdapter playerAdapter, Effect effect) {
		Player player = playerAdapter.getPlayer();
		
		if (player.getEffects().get(effect.getId()) > 0) {
			if (effect.getHitpoints() != 0) {
				int beforeHitpoints = player.getHitpoints();
				player.setHitpoints(beforeHitpoints + effect.getHitpoints());
				int deltaHitpoints = player.getHitpoints() - beforeHitpoints;
				
				if (effect.getHitpoints() < 0) {
					playerAdapter.getConnection().send(new Message(String.format("%s damages you by %d hitpoints.", effect.getName(), Math.abs(deltaHitpoints))));
				} else if (effect.getHitpoints() > 0) {
					playerAdapter.getConnection().send(new Message(String.format("%s heals you by %d hitpoints.", effect.getName(), Math.abs(deltaHitpoints))));
				}
			}
			
			//Diminui a duração do efeito
			int newDuration = player.getEffects().get(effect.getId()) - 1;
			if (newDuration <= 0) {
				player.getEffects().remove(effect.getId());
				if (effect.getDuration() > 1) playerAdapter.getConnection().send(new Message(String.format("Effects of %s wore off.", effect.getName())));
			}
			else player.getEffects().put(effect.getId(), newDuration);
		} else player.getEffects().remove(effect.getId());
	}
	
	public void damagePlayer(Enemy source, PlayerAdapter target) {
		Player player = target.getPlayer();
		
		//TODO: agility
		int beforeHitpoints = player.getHitpoints();
		int mitigatedDamage = (source.getDamage() - player.getArmor() < 0 ? 0 : source.getDamage() - player.getArmor());
		player.setHitpoints(beforeHitpoints - mitigatedDamage);
		int deltaHitpoints = player.getHitpoints() - beforeHitpoints;
		
		target.getConnection().send(new Message(String.format("%s attacks you for %d damage (%d absorbed)", source.getName(), Math.abs(deltaHitpoints), deltaHitpoints + mitigatedDamage)));
	}
	
	public void damageEnemy(PlayerAdapter source, Enemy target) {
		Player player = source.getPlayer();
		
		//TODO: agility
		int beforeHitpoints = target.getHitpoints();
		int mitigatedDamage = (player.getDamage() - target.getArmor() < 0 ? 0 : player.getDamage() - target.getArmor());
		target.setHitpoints(beforeHitpoints - (player.getDamage()));
		int deltaHitpoints = target.getHitpoints() - beforeHitpoints;
		
		source.getConnection().send(new Message(String.format("You attack %s for %d damage (%d absorbed)", target.getName(), Math.abs(deltaHitpoints), deltaHitpoints + mitigatedDamage)));
	}
}
