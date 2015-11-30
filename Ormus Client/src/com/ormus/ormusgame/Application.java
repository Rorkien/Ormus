package com.ormus.ormusgame;

import java.io.PrintStream;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ormus.ormusgame.ui.GameFrame;

public class Application {
	public static PrintStream out;	
	
	public Application() {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		new GameFrame();
	}

	public static void main(String[] args) {
		new Application();
	}
}
