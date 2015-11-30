package com.ormus.ormusgame.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.ormus.ormusgame.utils.Logger;

public class SpriteSheet {
	Map<Integer, Image> spriteSheet = new HashMap<Integer, Image>();
	Map<Integer, Image> itemsSheet = new HashMap<Integer, Image>();
	
	public SpriteSheet() {
		Logger.log(Logger.INFO, "Loading image sheets");
		Image[][] sheet = loadSheet("/sheet.png", 32, 32);
		Image[][] items = loadSheet("/items.png", 32, 32);
		
		//[X][Y]
		spriteSheet.put(1, sheet[0][0]);
		spriteSheet.put(2, sheet[1][0]);
		spriteSheet.put(3, sheet[2][0]);
		spriteSheet.put(4, sheet[3][0]);
		spriteSheet.put(5, sheet[4][0]);
		
		spriteSheet.put(6, sheet[5][0]);
		spriteSheet.put(7, sheet[6][0]);
		spriteSheet.put(8, sheet[7][0]);
		spriteSheet.put(9, sheet[8][0]);
		spriteSheet.put(10, sheet[9][0]);
		
		spriteSheet.put(11, sheet[5][1]);
		spriteSheet.put(12, sheet[6][1]);
		spriteSheet.put(13, sheet[7][1]);
		spriteSheet.put(14, sheet[9][1]);
		
		spriteSheet.put(15, sheet[5][2]);
		spriteSheet.put(16, sheet[6][2]);
		spriteSheet.put(17, sheet[7][2]);
		spriteSheet.put(18, sheet[8][2]);
		spriteSheet.put(19, sheet[9][2]);
		
		spriteSheet.put(20, sheet[0][1]);		
		spriteSheet.put(21, sheet[1][1]);
		
		spriteSheet.put(22, sheet[0][2]);
		
		spriteSheet.put(23, sheet[0][3]);
		spriteSheet.put(24, sheet[1][3]);
		spriteSheet.put(25, sheet[2][3]);
		spriteSheet.put(26, sheet[3][3]);
		spriteSheet.put(27, sheet[4][3]);
		spriteSheet.put(28, sheet[5][3]);
		spriteSheet.put(29, sheet[6][3]);
		
		spriteSheet.put(30, sheet[0][4]);
		spriteSheet.put(31, sheet[1][4]);
		spriteSheet.put(32, sheet[2][4]);
		spriteSheet.put(33, sheet[3][4]);
		spriteSheet.put(34, sheet[4][4]);
		spriteSheet.put(35, sheet[5][4]);
		spriteSheet.put(36, sheet[6][4]);
		spriteSheet.put(37, sheet[7][4]);
		
		spriteSheet.put(38, sheet[0][5]);
		spriteSheet.put(39, sheet[1][5]);
		spriteSheet.put(40, sheet[2][5]);
		spriteSheet.put(41, sheet[3][5]);
		spriteSheet.put(42, sheet[4][5]);
		
		spriteSheet.put(43, sheet[0][6]);
		spriteSheet.put(44, sheet[1][6]);
		spriteSheet.put(45, sheet[2][6]);
		spriteSheet.put(46, sheet[3][6]);
		spriteSheet.put(47, sheet[4][6]);
		spriteSheet.put(48, sheet[5][6]);
		
		spriteSheet.put(49, sheet[0][7]);
		spriteSheet.put(50, sheet[1][7]);
		spriteSheet.put(51, sheet[2][7]);
		
		spriteSheet.put(52, sheet[0][8]);
		spriteSheet.put(53, sheet[1][8]);
		spriteSheet.put(54, sheet[2][8]);
		spriteSheet.put(55, sheet[3][8]);
		
		spriteSheet.put(56, sheet[10][8]);
		spriteSheet.put(57, sheet[2][1]);
		spriteSheet.put(58, sheet[2][3]);
		
		for (int x = 0; x < items.length; x++) {
			for (int y = 0; y < items[x].length; y++) {
				itemsSheet.put(x + (y * items.length), items[x][y]);
			}
		}
	}
	
	public Image getTile(int tileId) {
		if (spriteSheet.get(tileId) == null) return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		else return spriteSheet.get(tileId);
	}
	
	public Image getItem(int id) {
		if (itemsSheet.get(id - 1) == null) return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		else return itemsSheet.get(id - 1);
	}
	
	public static Image[][] loadSheet(String filename, int sliceWidth, int sliceHeight) {
		BufferedImage fullImage;
		try {
			fullImage = ImageIO.read(Image.class.getResource(filename));
		} catch (IOException e) {
			throw new RuntimeException("Loading " + filename + " image failed");
		}

		int xSlices = fullImage.getWidth() / sliceWidth;
		int ySlices = fullImage.getHeight() / sliceHeight;

		Image slicedImage[][] = new Image[xSlices][ySlices];
		
		for (int x = 0; x < xSlices; x++) {
			for (int y = 0; y < ySlices; y++) {
				BufferedImage imageSlice = new BufferedImage(sliceWidth, sliceHeight, BufferedImage.TYPE_INT_ARGB);			
				imageSlice = fullImage.getSubimage(x * sliceWidth, y * sliceHeight, sliceWidth, sliceHeight);
				slicedImage[x][y] = imageSlice;
			}
		}
		return slicedImage;
	}
}
