package com.ormus.ormusgame.ui.console;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.ormus.ormusgame.utils.Localization;
import com.ormus.ormusgame.utils.Resources;

@SuppressWarnings("serial")
public class Menu extends JToolBar {
	
	JButton newButton = new JButton("");
	JButton openButton = new JButton("");
	JButton saveButton = new JButton("");
	JButton executeButton = new JButton("");
	JButton preferencesButton = new JButton("");
	
	public Menu(ScriptPane parent) {
		setFloatable(false);
		add(newButton);
		add(openButton);
		add(saveButton);
		add(executeButton);
		add(preferencesButton);
		
		newButton.setToolTipText(Localization.getString("button.newfile.tooltip"));
		openButton.setToolTipText(Localization.getString("button.openfile.tooltip"));
		saveButton.setToolTipText(Localization.getString("button.savefile.tooltip"));
		executeButton.setToolTipText(Localization.getString("button.executefile.tooltip"));
		preferencesButton.setToolTipText(Localization.getString("button.preferences.tooltip"));
		
		newButton.setIcon(new ImageIcon(Resources.getImage("button.newfile")));
		openButton.setIcon(new ImageIcon(Resources.getImage("button.openfile")));
		saveButton.setIcon(new ImageIcon(Resources.getImage("button.savefile")));
		executeButton.setIcon(new ImageIcon(Resources.getImage("button.executefile")));
		preferencesButton.setIcon(new ImageIcon(Resources.getImage("button.preferences")));
		
		newButton.setActionCommand("new");
		openButton.setActionCommand("open");
		saveButton.setActionCommand("save");
		executeButton.setActionCommand("execute");
		preferencesButton.setActionCommand("preferences");
		
		newButton.addActionListener(parent);
		openButton.addActionListener(parent);
		saveButton.addActionListener(parent);
		executeButton.addActionListener(parent);
		preferencesButton.addActionListener(parent);
	}

}
