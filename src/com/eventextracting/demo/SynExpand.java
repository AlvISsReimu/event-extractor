package com.eventextracting.demo;

import java.util.ArrayList;

import com.eventextracting.bean.ClassifiedWords;
import com.eventextracting.bean.Trigger;
import com.eventextracting.bean.TriggerCluster;

public class SynExpand {
	
	private static final int n = 2;
	
	public static ArrayList<ClassifiedWords> expSyn(ArrayList<TriggerCluster> clusters) {
		ArrayList<ClassifiedWords> dict = transClusters2ClassifiedWords(clusters);
		for (ClassifiedWords cw: dict){
			ArrayList<String> words = cw.getWords();
			ArrayList<String> waitlist = new ArrayList<String>();
			int length = words.size();
			for (int i = 0; i< length; i++){
				String word = words.get(i);
				ArrayList<String> codes = WordSimilarity.wordsEncode.get(word);
				if (codes == null)
					continue;
				for (String code: codes){
					ArrayList<String> syns = WordSimilarity.encodeWords.get(code);
					int counter = 0;
					for (String syn: syns){
						if (isInDict(syn, dict) && words.contains(syn))
							counter++;
					}
					if (counter >= n)
						waitlist.addAll(syns);
				}
			}
			words.addAll(waitlist);
			RemoveSame(words);
		}
		return dict;
	}
	
	private static ArrayList<ClassifiedWords> transClusters2ClassifiedWords(ArrayList<TriggerCluster> clusters) {
		ArrayList<ClassifiedWords> output = new ArrayList<ClassifiedWords>();
		for (TriggerCluster tc: clusters){
			ArrayList<Trigger> triggers = tc.getTriggers();
			ArrayList<String> ss = new ArrayList<String>();
			for (Trigger t: triggers)
				ss.add(t.getWord());
			ClassifiedWords words = new ClassifiedWords("", ss);
			output.add(words);
		}
		return output;
	}
	
	
	private static boolean isInDict(String word, ArrayList<ClassifiedWords> dict) {
		for (ClassifiedWords cw: dict){
			if (cw.getWords().contains(word))
				return true;
		}
		return false;
	}
	
    private static ArrayList RemoveSame(ArrayList list)
    {
        for (int i = 0; i < list.size() - 1; i++)
        {
            for (int j = i + 1; j < list.size(); j++)
            {
                if (list.get(i).equals(list.get(j)))
                {
                    list.remove(j);
                    j--;
                }
            }
        }
        return list;
    }
}
