package com.ormus.server.gamestate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ormus.server.Server;

/**
 * Classe responsável por ler o
 * Message of the Day do servidor
 */
public class MOTD {

	public static String[] getMOTD() {
		try {
			FileReader fileReader = new FileReader("assets/motd.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			List<String> lines = new ArrayList<String>();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.replaceAll("\\$\\{online\\}", "" + Server.getStatistics().getOnline());
				line = line.replaceAll("\\$\\{worlds\\}", "1. Vielt");		
				lines.add(line);
			}
			bufferedReader.close();
			
			return lines.toArray(new String[lines.size()]);
		} catch (FileNotFoundException e) {
			System.out.printf("Error opening MOTD file: %s", e.getMessage());
		} catch (IOException e) {
			System.out.printf("Error sending MOTD: %s", e.getMessage());
		}
		return null;
	}
}
