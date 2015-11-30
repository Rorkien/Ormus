package com.ormus.ormusgame.ui.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.ormus.ormusgame.utils.Localization;
import com.ormus.ormusgame.utils.PreferencesManager;
import com.ormus.ormusgame.utils.Resources;

@SuppressWarnings("serial")
public class PreferencesDialog extends JDialog {
	
	//languages
	//server and port
	JComboBox<String> languages = new JComboBox<String>();
	JTextField server = new JTextField(PreferencesManager.getPreferences("network", "masterserver"), 24);
	JTextField port = new JTextField(PreferencesManager.getPreferences("network", "port"), 8);
	JButton okButton = new JButton(Localization.getString("button.ok"));
	JButton cancelButton = new JButton(Localization.getString("button.cancel"));
	JButton defaultButton = new JButton(Localization.getString("button.default"));
	
	public PreferencesDialog(JPanel parent) {
		setTitle(Localization.getString("preferences.title"));
		setIconImage(Resources.getImage("icon"));
		setModal(true);
		
		languages.addItem("Português");
		languages.addItem("English");
		
		if (PreferencesManager.getPreferences("language", "currentlanguage").equals("ptBR")) languages.setSelectedItem("Português");
		else if (PreferencesManager.getPreferences("language", "currentlanguage").equals("enUS")) languages.setSelectedItem("English");
		
		JPanel dialogPanel = new JPanel();
		dialogPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		
		JPanel languagePanel = new JPanel();
		languagePanel.setBorder(new TitledBorder(Localization.getString("preferences.panel.language")));
		languagePanel.add(languages);
		
		JPanel serverPanel = new JPanel();
		serverPanel.setBorder(new TitledBorder(Localization.getString("preferences.panel.server")));
		serverPanel.add(server);
		serverPanel.add(port);
		
		JLabel restartLabel = new JLabel(Localization.getString("preferences.settingsapplied"));
		
		GroupLayout layout = new GroupLayout(dialogPanel);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(languagePanel).addComponent(serverPanel).addComponent(restartLabel).addGroup(Alignment.CENTER, layout.createSequentialGroup().addComponent(okButton).addComponent(cancelButton).addComponent(defaultButton)));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(languagePanel).addComponent(serverPanel).addComponent(restartLabel).addGroup(layout.createParallelGroup().addComponent(okButton).addComponent(cancelButton).addComponent(defaultButton)));
		dialogPanel.setLayout(layout);
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (languages.getSelectedItem().equals("Português")) PreferencesManager.setPreferences("language", "currentlanguage", "ptBR");
				else if (languages.getSelectedItem().equals("English")) PreferencesManager.setPreferences("language", "currentlanguage", "enUS");
				PreferencesManager.setPreferences("network", "masterserver", server.getText());
				PreferencesManager.setPreferences("network", "port", port.getText());
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		defaultButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				languages.setSelectedIndex(0);
				server.setText(PreferencesManager.getPreferences("network", "defaultserver"));
				port.setText(PreferencesManager.getPreferences("network", "defaultport"));
			}
		});
		
		add(dialogPanel);
		pack();
		setLocationRelativeTo(parent.getRootPane());
		setVisible(true);
	}

}
