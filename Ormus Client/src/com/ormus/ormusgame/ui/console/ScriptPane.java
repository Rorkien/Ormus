package com.ormus.ormusgame.ui.console;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.ormus.ormusgame.Application;
import com.ormus.ormusgame.ui.SerializablePreferences;
import com.ormus.ormusgame.utils.PreferencesManager;
import com.ormus.osl.Parser;
import com.ormus.osl.exceptions.ParsingException;

@SuppressWarnings("serial")
public class ScriptPane extends JPanel implements SerializablePreferences, ActionListener {
	Menu menuBar;
	ScriptArea scriptArea = new ScriptArea();
	JTextArea console = new JTextArea();
	JScrollPane consolePane = new JScrollPane(console);
	JSplitPane splitPane;
	Parser oslParser;

	public ScriptPane() {
		console.setFont(new Font("Courier New", Font.PLAIN, 12));
		
		Application.out = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				console.append(String.valueOf((char)(b & 0xFF)));
				consolePane.getVerticalScrollBar().setValue(consolePane.getVerticalScrollBar().getMaximum());
			}
		});
		
		menuBar = new Menu(this);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scriptArea, consolePane);
		splitPane.setDividerLocation(400);
		
		GroupLayout layout = new GroupLayout(this);
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(menuBar).addComponent(splitPane));
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(menuBar).addComponent(splitPane));
				
		setLayout(layout);
		add(menuBar);
		add(splitPane);
		
		PreferencesManager.addPreferencesListener(this);		
	}

	@Override
	public void saveFrameState() {
		PreferencesManager.setPreferences("game", "script.splitpos", String.valueOf(splitPane.getDividerLocation()));
		scriptArea.closeFile();
	}

	@Override
	public void loadFrameState() {
		if (PreferencesManager.getPreferences("game", "script.splitpos") != null) splitPane.setDividerLocation(Integer.valueOf(PreferencesManager.getPreferences("game", "script.splitpos")));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("new")) scriptArea.newFile();
		else if (e.getActionCommand().equals("open")) scriptArea.loadFile();
		else if (e.getActionCommand().equals("save")) scriptArea.saveFile();
		else if (e.getActionCommand().equals("preferences")) new PreferencesDialog(this);	
		else if (e.getActionCommand().equals("execute")) {
			String[] text = scriptArea.getText().split("\\r?\\n");
			try {
				oslParser = new Parser();
				oslParser.parse(text);
				oslParser.execute();
			} catch (ParsingException e1) {
				Application.out.println("ERROR: " + e1.getMessage());
			}
		}
	}
	
	
}
