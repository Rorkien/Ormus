package com.ormus.ormusgame.ui.console;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ormus.ormusgame.utils.Localization;
import com.ormus.ormusgame.utils.Logger;

@SuppressWarnings("serial")
public class ScriptArea extends JTextArea {
	private JFileChooser chooser = new JFileChooser();
	private FileNameExtensionFilter filter = new FileNameExtensionFilter(Localization.getString("file.osl"), "osl");
	private File currentFile;
	private boolean changed = false;
	
	public ScriptArea() {
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);
		setFont(new Font("Courier New", Font.PLAIN, 12));
		
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				changed = true;
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}
	
	public void newFile() {		
		closeFile();
		currentFile = null;
		changed = false;
		setText("");
	}
	
	public void closeFile() {
		if (changed) {
			if (JOptionPane.showConfirmDialog(this.getRootPane(), Localization.getString("alert.savebeforeclose"), Localization.getString("alert.savebeforeclose.title"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				saveFile();
			}
		}
	}
		
	public void loadFile() {
		closeFile();
		if (chooser.showOpenDialog(getRootPane()) == JFileChooser.APPROVE_OPTION) {
			currentFile = chooser.getSelectedFile();	
			changed = false;
			
			try {
				FileReader reader = new FileReader(currentFile);
				this.read(reader, this);
				reader.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(getRootPane(), Localization.getString("alert.errorsaving"), Localization.getString("alert.errortitle"), JOptionPane.ERROR_MESSAGE);
				Logger.log(Logger.INFO, String.format("Error saving file: %s (%s)", currentFile.getAbsolutePath(), e.getMessage()));
			}
			
			Logger.log(Logger.INFO, "Loading file: " + currentFile.getAbsolutePath());
		}
	}
	
	public void saveFile() {
		if (currentFile == null) {
			if (chooser.showSaveDialog(getRootPane()) == JFileChooser.APPROVE_OPTION) currentFile = chooser.getSelectedFile();
			
			//Se um arquivo foi escolhido
			if (currentFile != null) {
				//O arquivo escolhido já existe
				if (currentFile.exists()) {
					//Confirma a sobrescrita
					if (JOptionPane.showConfirmDialog(this.getRootPane(), Localization.getString("alert.overwrite"), Localization.getString("alert.alerttitle"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						Logger.log(Logger.INFO, "Overwriting file: " + currentFile.getAbsolutePath());
						forceSaveFile(currentFile);
					} else {
						currentFile = null;
						saveFile();
					}
				}
				
				forceSaveFile(currentFile);
				changed = false;
			}			
		} else {
			//Se o arquivo não existe, salva
			Logger.log(Logger.INFO, "Saving file: " + currentFile.getAbsolutePath());
			forceSaveFile(currentFile);
			changed = false;
		}
	}
	
	private void forceSaveFile(File file) {
		if (!file.getAbsolutePath().matches(".*\\.osl$")) file = new File(file.getAbsolutePath() + ".osl");
		
		try {
			FileWriter writer = new FileWriter(file);
			this.write(writer);
			writer.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(getRootPane(), Localization.getString("alert.errorsaving"), Localization.getString("alert.errortitle"), JOptionPane.ERROR_MESSAGE);
			Logger.log(Logger.INFO, String.format("Error saving file: %s (%s)", file.getAbsolutePath(), e.getMessage()));
		}
	}
}
