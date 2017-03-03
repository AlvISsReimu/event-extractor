package com.eventextracting.bean;

public class Trigger {
	
	private String word;
	private int freq;
	private String sub;
	private String obj;
	
	public Trigger(String word) {
		this.setWord(word);
		this.setFreq(1);
		this.setSub("");
		this.setObj("");
	}
	
	public Trigger(String word, int freq) {
		this.setWord(word);
		this.setFreq(freq);
		this.setSub("");
		this.setObj("");
	}
	
	public Trigger(String word, String sub, String obj){
		this.setWord(word);
		this.setFreq(1);
		this.setSub(sub);
		this.setObj(obj);
	}
	
	public String getWord() {
		return this.word;
	}
	
	public int getFreq() {
		return this.freq;
	}

	public String getSub() {
		return this.sub;
	}
	
	public String getObj() {
		return this.obj;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public void setSub(String sub) {
		this.sub = sub;
	}
	
	public void setObj(String obj) {
		this.obj = obj;
	}
	
	public void addFreq() {
		this.freq++;
	}
	
	@Override
	public boolean equals(Object obj) {
	    boolean bres = false;
	    if (obj instanceof Trigger) {
	    	Trigger o = (Trigger) obj;
	        bres = this.word.equals(o.word);
	    }
	    return bres;
	} 
	
}
