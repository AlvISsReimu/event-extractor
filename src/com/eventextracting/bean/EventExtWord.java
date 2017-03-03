package com.eventextracting.bean;

public class EventExtWord {
	
	public String trigger;
	public String sub;
	public String obj;
	public Features features;
	
	public EventExtWord(String t, String s, String o, Features f){
		trigger = t;
		sub = s;
		obj = o;
		features = f;
	}
	
	@Override
	public boolean equals(Object obj) {
	    boolean bres = false;
	    if (obj instanceof EventExtWord) {
	    	EventExtWord o = (EventExtWord) obj;
	        bres = this.trigger.equals(o.trigger)
	        		&& this.sub.equals(o.sub)
	        		&& this.obj.equals(o.obj)
	        		&& this.features.equals(o.features);
	    }
	    return bres;
	}
	
}
