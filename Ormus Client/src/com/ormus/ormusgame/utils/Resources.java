package com.ormus.ormusgame.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Resources {
	static Map<String, Image> images = new HashMap<String, Image>();
	
	static {
		Map<String, String> imagesString = PreferencesManager.getResources();
		
		try {	
			for (String identifier : imagesString.keySet()) {
				Image i;
				try {
					i = ImageIO.read(Resources.class.getResource(imagesString.get(identifier)));				
				} catch (IllegalArgumentException e) {
					i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
					Logger.log(Logger.ERROR, "Could not find resource: " + imagesString.get(identifier));
				}
				images.put(identifier, i);
			}
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "Error loading resources");
		}
		Logger.log(Logger.INFO, "Loaded " + images.size() + " resources.");
	}
	
	public static Image getImage(String identifier) {
		if (images.get(identifier) != null) return images.get(identifier);
		else {
			Logger.log(Logger.ERROR, "Could not find resource identified by: " + identifier);
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
	}
}
