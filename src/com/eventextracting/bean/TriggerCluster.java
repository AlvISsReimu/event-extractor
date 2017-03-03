package com.eventextracting.bean;

import java.util.ArrayList;

public class TriggerCluster {
	
	private String label;
	private ArrayList<Trigger> triggers;
	private int index;
	
	public TriggerCluster() {
		this.label = "";
		this.triggers = new ArrayList<Trigger>();
		this.index = 0;
	}
	
	public String getLabel() {
		return label;
	}
	
	public ArrayList<Trigger> getTriggers() {
		return triggers;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setTriggers(ArrayList<Trigger> triggers) {
		this.triggers = triggers;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void addTrigger(Trigger trigger) {
		this.triggers.add(trigger);
	}
	
	public boolean hasTrigger(Trigger trigger) {
		return this.triggers.contains(trigger);
	}
	
	public void genLabel() {
		int max = 0;
		String label = "";
		for (int i = 0; i < this.triggers.size(); i++){
			int freq = this.triggers.get(i).getFreq();
			if (freq > max){
				max = freq;
				label = this.triggers.get(i).getWord();
			}
		}
		this.setLabel(label);
	}
	
	public TriggerCluster combine(TriggerCluster tc) {
		ArrayList<Trigger> t = this.getTriggers();
		t.addAll(tc.getTriggers());
		TriggerCluster output = new TriggerCluster();
		output.setTriggers(t);
		return output;
	}
}
