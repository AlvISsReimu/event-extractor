package com.eventextracting.bean;

public class NlpirWord {
	
	private String cont;
	private String pos;
	
	public NlpirWord(String cont, String pos) {
		this.setCont(cont);
		this.setPos(pos);
	}
	
	public String getCont() {
		return this.cont;
	}
	
	public String getPos() {
		return this.pos;
	}

	public void setCont(String cont) {
		this.cont = cont;
	}
	
	public void setPos(String pos) {
		this.pos = pos;
	}
}
