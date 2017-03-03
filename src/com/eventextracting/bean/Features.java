package com.eventextracting.bean;

import java.util.ArrayList;

public class Features {
	
	public String trigger;
	public String pos;
	public String[] beforepos;
	public String[] afterpos;
	public String beforetype;
	public String aftertype;
	public String code;
	
	public Features() {}
	
	public Features(String trigger, String pos, String bp1, String bp2, String bp3, String bp4,
			String ap1, String ap2, String ap3, String ap4, String bt, String at, String code){
		this.trigger = trigger;
		this.pos = pos;
		this.beforepos = new String[]{bp1, bp2, bp3, bp4};
		this.afterpos = new String[]{ap1, ap2, ap3, ap4};
		this.beforetype = bt;
		this.aftertype = at;
		this.code = code;
	}
	
	public ArrayList<String> genFieldList(){
		ArrayList<String> fieldlist = new ArrayList<String>();
		fieldlist.add(trigger);
		fieldlist.add(pos);
		fieldlist.add(beforepos[0]);
		fieldlist.add(beforepos[1]);
		fieldlist.add(beforepos[2]);
		fieldlist.add(beforepos[3]);
		fieldlist.add(afterpos[0]);
		fieldlist.add(afterpos[1]);
		fieldlist.add(afterpos[2]);
		fieldlist.add(afterpos[3]);
		fieldlist.add(beforetype);
		fieldlist.add(aftertype);
		return fieldlist;
	}
	
	@Override
	public String toString(){
		return (trigger + " " +
				pos + " " +
				beforepos[0] + " " +
				beforepos[1] + " " +
				beforepos[2] + " " +
				beforepos[3] + " " +
				afterpos[0] + " " +
				afterpos[1] + " " +
				afterpos[2] + " " +
				afterpos[3] + " " +
				beforetype + " " +
				aftertype + " " +
				code
				);
	}
	
	@Override
	public boolean equals(Object obj) {
	    boolean bres = false;
	    if (obj instanceof Features) {
	    	Features o = (Features) obj;
	        bres = this.trigger.equals(o.trigger)
	        		&& this.pos.equals(o.pos)
	        		&& this.beforepos.equals(o.beforepos)
	        		&& this.afterpos.equals(o.afterpos)
	        		&& this.beforetype.equals(o.beforetype)
	        		&& this.aftertype.equals(o.aftertype);
	    }
	    return bres;
	}
	
}
