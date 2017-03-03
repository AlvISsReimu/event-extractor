package com.eventextracting.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.eventextracting.bean.ClassifiedWords;
import com.eventextracting.bean.Features;

public class MachineLearning {
	
	private static ArrayList<ClassifiedWords> dict = new ArrayList<ClassifiedWords>();
	
	public static void main(String[] args) throws Exception {
		System.out.println("开始扫描xml文件。");
		File file = new File("xml//");
		List<File> xmlflist = new ArrayList<File>();
		for (File folder: file.listFiles()){
			if (folder.isDirectory()){
				File[] filelist = folder.listFiles();
				for (File xmlf: filelist){
					if (xmlf.isFile() && xmlf.exists() && xmlf.getName().endsWith(".xml"))
						xmlflist.add(xmlf);
				}
			}
		}
		System.out.println("xml文件总数为" + xmlflist.size() + "个。");
		XmlPassage.readXmlsFromXmlFileList(xmlflist);
		
		System.out.println("开始加载词典。");
		readDict(new File("dict.txt"));
		System.out.println("加载词典成功。");
		genTrainTxt(new File("train.txt"));
		System.exit(0);
	}
	
	public static ArrayList<ClassifiedWords> getDict() {
		return dict;
	}
	
	public static int readDict(File file) {
		if (!file.exists() || !file.isFile())
			return 0;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
	    	BufferedReader bufferedReader = new BufferedReader(read);
	    	String s = null;
	    	String type = "";
	    	while((s = bufferedReader.readLine()) != null){
	    		if (s.equals(""))
	    			continue;
	    		if (s.contains("[")){
	    			type = s.substring(s.indexOf("[") + 1, s.length() - 1);
	    			continue;
	    		}
	    		String[] ss = s.split(" ");
	    		ArrayList<String> words = new ArrayList<String>();
	    		for (String word: ss){
	    			if (!word.equals(""))
	    				words.add(word);
	    		}
	    		dict.add(new ClassifiedWords(type, words));
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dict.size();
	}
	
	public static void genTrainTxt(File file) throws Exception {
		try{
	        if (file.exists())
	        	file.delete();
	        file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			int finished = 0;
			int block = 0;
			for (ClassifiedWords cw: dict){
				for (String word: cw.getWords()){
					ArrayList<Features> features = getFeaturesByWord(word);
					for (Features f: features){
						bw.write(cw.getType() + " ");
						bw.write(f.toString());
						bw.newLine();
					}
				}
				finished++;
	            int temp_block = 50 * finished / dict.size();
	            for (int k = 0; k < (temp_block - block); k++)
	            	System.out.print("■");
	            block = temp_block;
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static ArrayList<Features> getFeaturesByWord(String word) {
		ArrayList<String> xmls = XmlPassage.getXmls();
		ArrayList<Features> features = new ArrayList<Features>();
		for (String xml: xmls){
			try {
				Document document = DocumentHelper.parseText(xml);
				Element root = document.getRootElement();
				Element doc = root.element("doc");
				for(Iterator it_para = doc.elementIterator(); it_para.hasNext();){
					Element para_node = (Element) it_para.next();
					for(Iterator it_sent = para_node.elementIterator(); it_sent.hasNext();){
						Element sent_node = (Element) it_sent.next();
						for(Iterator it_word = sent_node.elementIterator(); it_word.hasNext();){
							Element word_node = (Element) it_word.next();
							if (word_node.attribute("cont").getText().equals(word))
								features.add(TriggerExtracting.getFeature(sent_node, word_node));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return features;
	}
}
