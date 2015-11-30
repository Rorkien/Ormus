package com.ormus.ormusgame.ui.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Arrays;

import javax.swing.JPanel;

import com.ormus.ormusgame.graphics.Renderer;
import com.ormus.ormusgame.graphics.SpriteSheet;
import com.ormus.ormusgame.networking.Client;
import com.ormus.ormusgame.utils.Resources;
import com.ormus.shared.entity.Enemy;
import com.ormus.shared.entity.Player;

@SuppressWarnings("serial")
public class GamePane extends JPanel {
	Renderer renderer;
	SpriteSheet spriteSheet;
	Client client;
	
	public GamePane() {
		setMinimumSize(new Dimension(352, 288));
		renderer = new Renderer(352, 288);
		spriteSheet = new SpriteSheet();
		setBackground(Color.BLACK);
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public void render() {
		int w = getWidth();
		int h = getHeight();
		int scale = 1;
		
		Arrays.fill(renderer.getPixels(), 0);
		
		if (getWidth() * (3 / 4D) <= getHeight()) h = (int) (getWidth() * (3 / 4D));
		else w = (int) (getHeight() * (4 / 3D));		
		if (client.getCurrentSnapshot() == null || !client.isConnected()) {
			renderer.draw(Resources.getImage("splash"), 0, 0);
			scale = 2;
		}
		else {					
			Integer[] tilesId = client.getCurrentSnapshot().getLevelTiles();
			Integer[] itemsId = client.getCurrentSnapshot().getLevelItems();
			
			//Desenha o mundo
			for (int i = 0; i < tilesId.length; i++) renderer.draw(spriteSheet.getTile(tilesId[i]), (i * 32) % 352, (i / 11) * 32);
			for (int i = 0; i < itemsId.length; i++) {
				renderer.draw(spriteSheet.getItem(itemsId[i]), (i * 32) % 352, (i / 11) * 32);
			}
			
			Player self = client.getCurrentSnapshot().getSelf();
			Point selfPosition = self.getPosition();
			
			//Desenha os sprites do jogador, outros jogadores e de criaturas
			renderer.draw(spriteSheet.getItem(57), (5 * 32), (4 * 32));
			for (int i = 0; i < client.getCurrentSnapshot().getOther().length; i++) {
				Player currentPlayer = client.getCurrentSnapshot().getOther()[i];
				if (currentPlayer != null) renderer.draw(spriteSheet.getItem(57), ((currentPlayer.getPosition().x - selfPosition.x + 5) * 32), ((currentPlayer.getPosition().y - selfPosition.y + 4) * 32));
			}
			
			for (int i = 0; i < client.getCurrentSnapshot().getEnemies().length; i++) {
				Enemy currentEnemy = client.getCurrentSnapshot().getEnemies()[i];
				if (currentEnemy != null) {
					int spriteId = 0;
					if (currentEnemy.getName().equals("Rabbit")) spriteId = 60;
					if (currentEnemy.getName().equals("Wolf")) spriteId = 59;
					renderer.draw(spriteSheet.getItem(spriteId), ((currentEnemy.getPosition().x - selfPosition.x + 5) * 32), ((currentEnemy.getPosition().y - selfPosition.y + 4) * 32));
				}
			}

			//Desenha os nameplates do jogador, outros jogadores, e de criaturas
			renderer.drawNameplate(self, (5 * 32), (4 * 32));
			for (int i = 0; i < client.getCurrentSnapshot().getOther().length; i++) {
				Player currentPlayer = client.getCurrentSnapshot().getOther()[i];
				renderer.drawNameplate(currentPlayer, ((currentPlayer.getPosition().x - selfPosition.x + 5) * 32), ((currentPlayer.getPosition().y - selfPosition.y + 4) * 32) - 6);
			}
			
			for (int i = 0; i < client.getCurrentSnapshot().getEnemies().length; i++) {
				Enemy currentEnemy = client.getCurrentSnapshot().getEnemies()[i];
				renderer.drawNameplate(currentEnemy, ((currentEnemy.getPosition().x - selfPosition.x + 5) * 32), ((currentEnemy.getPosition().y - selfPosition.y + 4) * 32) - 6);
			}
			
		}
		
		Graphics g = getGraphics();
		g.drawImage(renderer.getImage(), (getWidth() - w) / 2, (getHeight() - h) / 2, w * scale, h * scale, null);
	}
}
