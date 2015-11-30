package com.ormus.ormusgame.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.ormus.shared.entity.Creature;

public class Renderer {
	BufferedImage raster;
	int[] rasterPixels;
	int width, height;

	public Renderer(int width, int height) {
		this.width = width;
		this.height = height;
		raster = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		rasterPixels = ((DataBufferInt) raster.getRaster().getDataBuffer()).getData();	
	}
	
	public int[] getPixels() {
		return rasterPixels;
	}
	
	public BufferedImage getImage() {
		return raster;
	}
	
	public void draw(Image i, int x, int y) {
		Graphics g = raster.getGraphics();
		g.drawImage(i, x, y, null);
		g.dispose();
	}
	
	public void draw(int pixels[], int x, int y, int w, int h) {
		for (int yp = 0; yp < h; yp++) {
			int yPix = yp + y;
			if (yPix < 0 || yPix >= height) continue;

			for (int xp = 0; xp < w; xp++) {
				int xPix = xp + x;
				if (xPix < 0 || xPix >= width) continue;
				rasterPixels[xPix + yPix * width] = pixels[xp + yp * w];	
			}
		}
	}
	
	public void drawNameplate(Creature creature, int x, int y) {
		Graphics g = raster.getGraphics();
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
		g.drawString(creature.getName(), x, y);
		g.fillRect(x + 1, y + 3, 30, 5);
		
		float healthFactor = creature.getHitpoints() / (creature.getMaxHitpoints() * 1F);
		
		if (healthFactor >= 0.66) g.setColor(Color.GREEN);
		else if (healthFactor >= 0.33) g.setColor(Color.ORANGE);
		else if (healthFactor >= 0) g.setColor(Color.RED);
		else g.setColor(Color.BLACK);
		g.fillRect(x + 2, y + 4, (int)(28 * healthFactor), 3);
		g.dispose();
	}
}
