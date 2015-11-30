package com.ormus.ormusgame.utils.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.ormus.ormusgame.utils.Logger;

public class INIFile {
	private Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
	
	public INIFile(String filename) {
		load(filename);
	}
	
	public String read(String section, String key) {
		Map<String, String> sectionMap = map.get(section);
		
		if (sectionMap != null) return sectionMap.get(key);
		else return null;
	}
	
	public void write(String section, String key, String value) {
		Map<String, String> sectionMap = map.get(section);

		if (sectionMap == null) map.put(section, new HashMap<String, String>());
		map.get(section).put(key, value);		
	}
	
	public Map<String, String> getSection(String section) {
		Map<String, String> sectionMap = map.get(section);
		
		if (sectionMap != null) return sectionMap;
		else return null;
	}
	
	private void load(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String in, currentSection = null;
			
			while ((in = reader.readLine()) != null) {
				if (!in.equals("")) {
					if (in.charAt(0) == '[' && in.charAt(in.length() - 1) == ']') {
						currentSection = in.substring(1, in.length() - 1);
					}
					else if (currentSection != null && in.split("=").length == 2) {
						write(currentSection, in.split("=")[0], in.split("=")[1]);
					}	
				}
			}
			
			reader.close();
			
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "could not load INI file! (" + e.getMessage() + ")");
		}		
	}
	
	public void save(String filename) {
		try {
			PrintWriter writer = new PrintWriter(filename);

			for (String section : map.keySet()) {
				writer.println("[" + section + "]");

				Map<String, String> keyValue = map.get(section);				
				for (String key : keyValue.keySet()) {
					writer.println(key + "=" + keyValue.get(key));
				}				

				writer.println();				
			}			
			writer.close();

		} catch (FileNotFoundException e) {
			Logger.log(Logger.ERROR, "could not save INI file! (" + e.getMessage() + ")");
		}		
	}
}
