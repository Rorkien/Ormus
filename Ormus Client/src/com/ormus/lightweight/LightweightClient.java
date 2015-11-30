package com.ormus.lightweight;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.ormus.ormusgame.Application;
import com.ormus.ormusgame.networking.Client;
import com.ormus.shared.entity.Action;
import com.ormus.shared.entity.actions.MoveAction;
import com.ormus.shared.gamestate.AuthenticationSignal;
import com.ormus.shared.gamestate.CreateSignal;

public class LightweightClient extends Thread {
	long startTime = System.currentTimeMillis();
	SimpleDateFormat timestamp = new SimpleDateFormat("[HH:mm:ss.SSS]");
	
	String name;
	String password;
	
	public String getTimestamp() {
		long now = System.currentTimeMillis() - startTime - (21 * 60 * 60 * 1000);
		return timestamp.format(new Date(now));
	}
	
	public void log(String message) {
		System.out.println(getTimestamp() + " " + message);
	}
	
	public void run() {
		Application.out = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
			}
		});
		
		List<Action> actions = new ArrayList<Action>();
		
		if (name == null) name = "Rorkien";
		if (password == null) password = "888666";
		
		actions.add(new MoveAction(0));
		actions.add(new MoveAction(1));
		actions.add(new MoveAction(2));
		actions.add(new MoveAction(3));
		
		Random rng = new Random();
		
		log("Starting Ormus Lightweight Client");
		try {
			Client client = new Client();
			Thread.sleep(1000);
			client.send(new CreateSignal(name, password, "1"));
			Thread.sleep(1000);
			client.send(new AuthenticationSignal(name, password, "1"));
			Thread.sleep(1000);
			
			while (true) {
				client.send(actions.get(rng.nextInt(actions.size())));
				Thread.sleep(30000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LightweightClient(String name, String password) {
		this.name = name;
		this.password = password;
		start();
	}
	
	public static void main(String[] args) {
		new LightweightClient(null, null);
	}

}
