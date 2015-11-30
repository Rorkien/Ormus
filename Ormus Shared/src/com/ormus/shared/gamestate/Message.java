package com.ormus.shared.gamestate;

import java.io.Serializable;

public class Message implements Serializable {
	String contents;
	
	public Message(String contents) {
		this.contents = contents;
	}
	
	public String getContents() {
		return contents;
	}

}
