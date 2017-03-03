package com.eventextracting.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.eventextracting.bean.MatrixElement;
import com.eventextracting.bean.Trigger;
import com.eventextracting.bean.TriggerCluster;

public class TriggerClustering {
	
	private static ArrayList<TriggerCluster> clusters = new ArrayList<TriggerCluster>();
	private static ArrayList<MatrixElement> matrix = new ArrayList<MatrixElement>();
	private static ArrayList<Integer> deletelist = new ArrayList<Integer>();
	
	private static final double t = 0.9;
	
	public static ArrayList<TriggerCluster> getClusters() {
		return clusters;
	}
	
	public static ArrayList<TriggerCluster> clusTriggers(ArrayList<Trigger> triggers) {
		System.out.println("开始触发词聚类。");
		ArrayList<TriggerCluster> cs = new ArrayList<TriggerCluster>();
		for (int i = 0; i < triggers.size(); i++){
			TriggerCluster cluster = new TriggerCluster();
			cluster.addTrigger(triggers.get(i));
			cluster.setIndex(i);
			cs.add(cluster);
		}
		int size = cs.size();
		int total = size * (size - 1) / 2;
		int block = 0;
		int finished = 0;
		for (int i = 0; i < 50; i++)
			System.out.print("-");
		System.out.print("\n");
		for (int i = 1; i < size; i++){
			for (int j = 0; j < i; j++){
				MatrixElement e = new MatrixElement(i, j, WordSimilarity.getSimilarity(cs.get(i), cs.get(j)));
				matrix.add(e);
				finished++;
	            int temp_block = 50 * finished / total;
	            for (int k = 0; k < (temp_block - block); k++)
	            	System.out.print("■");
	            block = temp_block;
			}
		}
		System.out.print("\n");
		clusters = recursionClustering(cs);
		for (int i = 0; i < clusters.size(); i++){
			if (deletelist.contains(clusters.get(i).getIndex())){
				clusters.remove(i);
				i--;
			}
		}
		genLabel4Clusters();
		System.out.println("触发词聚类完毕。");
		return clusters;
		/*
		for (int i = 0; i < triggers.size(); i++){
			Trigger trigger1 = triggers.get(i);
			int index1 = findTriggerInClusters(trigger1);
			if (index1 == -1){
				TriggerCluster newcluster = new TriggerCluster();
				newcluster.addTrigger(trigger1);
				clusters.add(newcluster);
				index1 = clusters.size() - 1;
			}
			TriggerCluster cluster1 = clusters.get(index1);
			for (int j = i + 1; j < triggers.size(); j++){
				Trigger trigger2 = triggers.get(j);
				int index2 = findTriggerInClusters(trigger2);
				if (!cluster1.hasTrigger(trigger2)){
					double similarity = WordSimilarity.getSimilarity(trigger1.getWord(), trigger2.getWord());
					//System.out.println(trigger1.getWord() + "-" + trigger2.getWord() + ": " + similarity);
					if (similarity > t){
						if (index2 != -1){
							TriggerCluster cluster2 = clusters.get(index2);
							cluster2.getTriggers().remove(trigger2);
						}
						cluster1.addTrigger(trigger2);
					}
				}
			}
		}
		genLabel4Clusters();
		return clusters;
		*/
		
	}
	
	private static ArrayList<TriggerCluster> recursionClustering(ArrayList<TriggerCluster> cs) {
		//System.out.println("cs = " + cs.size() + ", dl = " + deletelist.size());
		double max_sim = 0.0;
		int max_x = -1;
		int max_y = -1;
		for (int i = 0; i < matrix.size(); i++){
			int x = matrix.get(i).x;
			int y = matrix.get(i).y;
			double sim = matrix.get(i).sim;
			if (sim >= max_sim){
				max_sim = sim;
				max_x = x;
				max_y = y;
			}
		}
		if (max_sim <= t)
			return cs;
		else{
			int index = cs.size();
			TriggerCluster newcluster = cs.get(max_x).combine(cs.get(max_y));
			newcluster.setIndex(index);
			cs.add(newcluster);
			for (int i = 0; i < matrix.size(); i++){
				int x = matrix.get(i).x;
				int y = matrix.get(i).y;
				if ((x == max_x)||(x == max_y)||(y == max_x)||(y == max_y)){
					matrix.remove(i);
					i--;
				}
			}
			deletelist.add(max_x);
			deletelist.add(max_y);
			for (int i = 0; i < index - 1; i++){
				if (!deletelist.contains(i)){
					MatrixElement e = new MatrixElement(index, i, WordSimilarity.getSimilarity(cs.get(index), cs.get(i)));
					matrix.add(e);
				}
			}
			System.out.println("剩余触发词类数：" + (cs.size() - deletelist.size()));
			return recursionClustering(cs);
		}
		/*
		int size = cs.size();
		if (size == 0)
			return new ArrayList<TriggerCluster>();
		double max_sim = 0.0;
		int max_i = -1;
		int max_j = -1;
		System.out.println(size);
		for (int i = 0; i < size; i++){
			TriggerCluster cluster1 = cs.get(i);
			for (int j = i + 1; j < size; j++){
				TriggerCluster cluster2 = cs.get(j);
				double similarity = WordSimilarity.getSimilarity(cluster1, cluster2);
				if (similarity >= max_sim){
					max_sim = similarity;
					max_i = i;
					max_j = j;
					if (similarity >= 0.9)
						break;
				}
				if (similarity >= 0.9)
					break;
			}
		}
		if (max_sim > t){
			TriggerCluster newcluster = cs.get(max_i).combine(cs.get(max_j));
			cs.remove(max_j);
			cs.remove(max_i);
			cs.add(newcluster);
			return recursionClustering(cs);
		}
		else
			return cs;
			*/
	}
	/*
	private static int findTriggerInClusters(Trigger trigger){
		int index = -1;
		for (int i = 0; i < clusters.size(); i++){
			if (clusters.get(i).getTriggers().contains(trigger)){
				index = i;
				break;
			}
		}
		return index;
	}*/
	
	private static void genLabel4Clusters(){
		for (TriggerCluster c: clusters)
			c.genLabel();
	}
	
	public static boolean saveClusters(File file){
		try{
	        if (file.exists())
	        	file.delete();
	        file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			for (TriggerCluster c: clusters){
				bw.write(c.getLabel() + ": ");
				for (Trigger cc: c.getTriggers())
					bw.write(cc.getWord() + " ");
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static int readClusters(File file) {
		if (!file.exists() || !file.isFile())
			return 0;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
	    	BufferedReader bufferedReader = new BufferedReader(read);
	    	String s = null;
	    	while((s = bufferedReader.readLine()) != null){
	    		int index = s.indexOf(":");
	    		String label = s.substring(0, index);
	    		String words = s.substring(index + 1);
	    		String[] ss = words.split(" ");
	    		ArrayList<Trigger> triggers = new ArrayList<Trigger>();
	    		for (String word: ss){
	    			if (!word.equals("")){
	    				Trigger trigger = new Trigger(word);
		    			triggers.add(trigger);
	    			}
	    		}
	    		TriggerCluster cluster = new TriggerCluster();
	    		cluster.setLabel(label);
	    		cluster.setTriggers(triggers);
	    		clusters.add(cluster);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clusters.size();
	}
}
