package com.eventextracting.bean;

import java.util.ArrayList;

public class ClassifiedWords {
	
	private String type;
	private ArrayList<String> words;
	
	public ClassifiedWords(String type, ArrayList<String> words) {
		this.type = type;
		this.words = words;
	}
	
	public String getType() {
		return this.type;
	}
	
	public ArrayList<String> getWords() {
		return this.words;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setWords(ArrayList<String> words) {
		this.words = words;
	}
	
	@Override
	public boolean equals(Object obj) {
	    boolean bres = false;
	    if (obj instanceof ClassifiedWords) {
	    	ClassifiedWords o = (ClassifiedWords) obj;
	        bres = this.type.equals(o.type) && this.words == o.words;
	    }
	    return bres;
	}
	
	@Override
	public String toString() {
		String s = this.type + "\n";
		for (String ss: this.words)
			s += (ss + " ");
		return s;
	}
	
}
