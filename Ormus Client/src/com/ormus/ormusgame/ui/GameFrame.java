package com.ormus.ormusgame.ui;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.ormus.ormusgame.networking.Client;
import com.ormus.ormusgame.ui.console.ScriptPane;
import com.ormus.ormusgame.ui.game.GamePane;
import com.ormus.ormusgame.utils.Logger;
import com.ormus.ormusgame.utils.PreferencesManager;
import com.ormus.ormusgame.utils.Resources;

@SuppressWarnings("serial")
public class GameFrame extends JFrame implements WindowListener, SerializablePreferences {
	private JSplitPane splitPane;
	private GamePane gamePane = new GamePane();
	private ScriptPane scriptPane = new ScriptPane();
	
	Client client;
	
	public GameFrame() {
		setTitle("Ormus");
		setIconImage(Resources.getImage("icon"));

		client = new Client();
		gamePane.setClient(client);	
		
		addWindowListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(800, 600));
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gamePane, scriptPane);
		splitPane.setDividerLocation(300);
		add(splitPane);
		
		PreferencesManager.addPreferencesListener(this);
		setVisible(true);
		
		Logger.log(Logger.INFO, "Game frame started");
		
		while (true) {
			gamePane.render();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Logger.log(Logger.ERROR, String.format("Error: ", e.getMessage()));
			}
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		PreferencesManager.fireSavePreferencesEvent();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		PreferencesManager.fireSavePreferencesEvent();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void saveFrameState() {
		Logger.log(Logger.INFO, "Saving frame state");
		if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			PreferencesManager.setPreferences("game", "fx", String.valueOf(getBounds().x));
			PreferencesManager.setPreferences("game", "fy", String.valueOf(getBounds().y));
			PreferencesManager.setPreferences("game", "fw", String.valueOf(getBounds().width));
			PreferencesManager.setPreferences("game", "fh", String.valueOf(getBounds().height));
		}
		PreferencesManager.setPreferences("game", "fstate", String.valueOf(getExtendedState()));
		PreferencesManager.setPreferences("game", "splitpos", String.valueOf(splitPane.getDividerLocation()));
	}

	@Override
	public void loadFrameState() {
		Logger.log(Logger.INFO, "Loading frame state");
		if (PreferencesManager.getPreferences("game", "fstate") != null) setExtendedState(Integer.valueOf(PreferencesManager.getPreferences("game", "fstate")));
		if (PreferencesManager.getPreferences("game", "fx") != null) setBounds(Integer.valueOf(PreferencesManager.getPreferences("game", "fx")), Integer.valueOf(PreferencesManager.getPreferences("game", "fy")), Integer.valueOf(PreferencesManager.getPreferences("game", "fw")), Integer.valueOf(PreferencesManager.getPreferences("game", "fh")));
		if (PreferencesManager.getPreferences("game", "splitpos") != null) splitPane.setDividerLocation(Integer.valueOf(PreferencesManager.getPreferences("game", "splitpos")));
	}
}
