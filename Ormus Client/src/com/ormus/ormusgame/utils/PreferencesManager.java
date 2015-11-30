package com.ormus.ormusgame.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ormus.ormusgame.ui.SerializablePreferences;
import com.ormus.ormusgame.utils.parser.INIFile;

public class PreferencesManager {
	private static INIFile preferences = new INIFile("prefs.ini");
	static List<SerializablePreferences> listeners = new ArrayList<SerializablePreferences>();
	
	public static String getPreferences(String section, String key) {
		return preferences.read(section, key);
	}
	
	public static void setPreferences(String section, String key, String value) {
		preferences.write(section, key, value);
	}

	public static void addPreferencesListener(SerializablePreferences listener) {
		listeners.add(listener);
		listener.loadFrameState();
	}
	
	public static void fireSavePreferencesEvent() {
		for (SerializablePreferences listener : listeners) listener.saveFrameState();
		preferences.save("prefs.ini");
	}
	
	//--
	
	public static Map<String, String> getResources() {
		return preferences.getSection("resources");
	}
	
	public static String getCurrentLanguage() {
		if (preferences.read("language", "currentlanguage") != null) return preferences.read("language", "currentlanguage");
		else {
			Logger.log(Logger.ERROR, "Current language not set, using default.");
			return preferences.read("language", "defaultlanguage");
		}
	}
	
	public static String getDefaultLanguage() {
		if (preferences.read("language", "defaultlanguage") != null) return preferences.read("language", "defaultlanguage");
		else {
			Logger.log(Logger.ERROR, "Could not find default language, using null.");
			return null;
		}
	}
}
