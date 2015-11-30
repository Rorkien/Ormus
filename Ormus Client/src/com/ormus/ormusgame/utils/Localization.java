package com.ormus.ormusgame.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Localization {
	static Map<String, String> localization = new HashMap<String, String>();
	
	static {
		reloadLocalization(PreferencesManager.getCurrentLanguage());
	}
	
	public static String getString(String identifier) {
		if (localization.get(identifier) == null) return identifier;
		else return localization.get(identifier);
	}
	
	public static void reloadLocalization(String languageIdentifier) {
		boolean localizationExists = true;
		
		if (Localization.class.getResource("/localization/" + languageIdentifier + ".txt") == null) {
			String defaultLanguageIdentifier = PreferencesManager.getDefaultLanguage();
			if (Localization.class.getResource("/localization/" + defaultLanguageIdentifier + ".txt") == null) {
				localizationExists = false;
				Logger.log(Logger.ERROR, "Default Localization " + defaultLanguageIdentifier + " not found. Using null.");
			}
			else {
				Logger.log(Logger.ERROR, "Localization " + languageIdentifier + " not found. Using default.");
				languageIdentifier = defaultLanguageIdentifier;
			}
		}
		
		if (localizationExists) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(Localization.class.getResourceAsStream("/localization/" + languageIdentifier + ".txt")));

				String in;
				while ((in = reader.readLine()) != null) {
					if (in.trim().equals("")) continue;
					String[] split = in.split("=");
					localization.put(split[0], split[1]);
				}
			} catch (IOException e) {
				Logger.log(Logger.INFO, "Could not read from localization file.");
			}
			Logger.log(Logger.INFO, "Loaded " + localization.size() + " entries from localization " + languageIdentifier + ".");
		}
	}

}
