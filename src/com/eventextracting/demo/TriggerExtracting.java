package com.eventextracting.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.eventextracting.bean.EventExtWord;
import com.eventextracting.bean.Features;
import com.eventextracting.bean.NlpirWord;
import com.eventextracting.bean.Trigger;
import com.lingjoin.demo.NlpirMethod;

public class TriggerExtracting {
	
	private static ArrayList<Trigger> triggers = new ArrayList<Trigger>();
	private static ArrayList<EventExtWord> subobjs = new ArrayList<EventExtWord>();
	
	private final static List<String> verblist = Arrays.asList("v", "vi", "vn");
	
	public static ArrayList<Trigger> getTriggers() {
		return triggers;
	}
	
	public static ArrayList<EventExtWord> getSubobjs() {
		return subobjs;
	}
	
	public static ArrayList<Trigger> extTrigger(String passage, String filename, boolean isGenDict) {
		ArrayList<String> xmls = XmlPassage.getXmls(passage, filename);
		int nonverb = 0;
		int trigger_num = 0;
		try {
			System.out.println("开始触发词抽取。");
			for (String xml: xmls){
				Document document = DocumentHelper.parseText(xml);
				Element root = document.getRootElement();
				Element doc = root.element("doc");
				for(Iterator it_para = doc.elementIterator(); it_para.hasNext();){
					Element para_node = (Element) it_para.next();
					for(Iterator it_sent = para_node.elementIterator(); it_sent.hasNext();){
						Element sent_node = (Element) it_sent.next();
						ArrayList<NlpirWord> nlpirwords = tagSentenceByNlpir(sent_node.attribute("cont").getText());
						ArrayList<Integer> num_SBV = new ArrayList<Integer>();
						ArrayList<Integer> num_VOB = new ArrayList<Integer>();
						for(Iterator it_word = sent_node.elementIterator(); it_word.hasNext();){
							Element word_node = (Element) it_word.next();
							if (word_node.attribute("relate").getText().equals("SBV")){
								int parent = Integer.parseInt(word_node.attribute("parent").getText());
								if (!num_SBV.contains(parent))
									num_SBV.add(parent);
							}
							else if (word_node.attribute("relate").getText().equals("VOB")){
								int parent = Integer.parseInt(word_node.attribute("parent").getText());
								if (!num_VOB.contains(parent))
									num_VOB.add(parent);
							}
						}
						for (int i: num_SBV){
							if (num_VOB.contains(i)){
								for(Iterator it_word = sent_node.elementIterator(); it_word.hasNext();){
									Element word_node = (Element) it_word.next();
									if (Integer.parseInt(word_node.attribute("id").getText()) == i){
										if (!isGenDict && isVerb(nlpirwords, word_node.attribute("cont").getText())){
											EventExtWord eew = new EventExtWord(word_node.attribute("cont").getText(), "", "",
													getFeature(sent_node, word_node));
											String trigger_id = word_node.attribute("id").getText();
											for(Iterator it_word2 = sent_node.elementIterator(); it_word2.hasNext();){
												Element word_node2 = (Element) it_word2.next();
												if (word_node2.attribute("parent").getText().equals(trigger_id) &&
														word_node2.attribute("relate").getText().equals("SBV"))
													eew.sub = word_node2.attribute("cont").getText();
												else if (word_node2.attribute("parent").getText().equals(trigger_id) &&
														word_node2.attribute("relate").getText().equals("VOB"))
													eew.obj = word_node2.attribute("cont").getText();
											}
											if (!subobjs.contains(eew))
												subobjs.add(eew);
										}
										
										Trigger trigger = new Trigger(word_node.attribute("cont").getText());
										int index = -1;
										for (int j = 0; j < triggers.size(); j++){
											if (triggers.get(j).getWord().equals(trigger.getWord())){
												index = j;
												break;
											}
										}
										if (index == -1){
											if (isVerb(nlpirwords, trigger.getWord())){
												triggers.add(trigger);
												trigger_num++;
											}
											else
												nonverb++;
										}
										else
											triggers.get(index).addFreq();
										break;
									}
								}
							}
						}
					}
				}
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("成功抽取" + trigger_num + "个触发词。");
		//System.out.println("通过NLPIR的POS Tagger排除了" + nonverb + "个其他动词。");
		return triggers;
	}
	
	private static ArrayList<NlpirWord> tagSentenceByNlpir(String sentence) {
		String result = NlpirMethod.NLPIR_ParagraphProcess(sentence, 1);
		String[] wordtags = result.split(" ");
		ArrayList<NlpirWord> nlpirwords = new ArrayList<NlpirWord>();
		for (int i = 0; i < wordtags.length; i++){
			int index = wordtags[i].indexOf("/");
			if (index >= 0){
				NlpirWord nlpirword = new NlpirWord(wordtags[i].substring(0, index), wordtags[i].substring(index + 1));
				nlpirwords.add(nlpirword);
			}
		}
		return nlpirwords;
	}
	
	private static boolean isVerb(ArrayList<NlpirWord> nlpwords, String word) {
		//这里要添加banlist
		if (word.equals("是")||word.equals("为")||word.equals("应该")||word.equals("到"))
			return false;
		for (NlpirWord w: nlpwords){
			if (w.getCont().equals(word)){
				if (!verblist.contains(w.getPos()))
					return false;
			}
		}
		return true;
	}
	
	public static int readTriggers(File file) {
		if (!file.exists() || !file.isFile())
			return 0;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
	    	BufferedReader bufferedReader = new BufferedReader(read);
	    	String s = null;
	    	while((s = bufferedReader.readLine()) != null){
	    		String[] ss = s.split(" ");
	    		Trigger t = new Trigger(ss[0], Integer.valueOf(ss[1]));
	    		triggers.add(t);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return triggers.size();
	}
	
	public static boolean saveTriggers(File file) {
		try{
	        if (file.exists())
	        	file.delete();
	        file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			for (Trigger t: TriggerExtracting.getTriggers()){
				bw.write(t.getWord() + " " + t.getFreq());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Features getFeature(Element sent_node, Element word_node) {
		String trigger = word_node.attribute("cont").getText();
		String pos = word_node.attribute("pos").getText();
		int id = Integer.valueOf(word_node.attribute("id").getText());
		String bp[] = new String[]{getPos(sent_node, id - 4), getPos(sent_node, id - 3),
									getPos(sent_node, id - 2), getPos(sent_node, id - 1)};
		String ap[] = new String[]{getPos(sent_node, id + 1), getPos(sent_node, id + 2),
									getPos(sent_node, id + 3), getPos(sent_node, id + 4)};
		String[] types = getType(sent_node, id);
		String bt = types[0];
		String at = types[1];
		return new Features(trigger, pos, bp[0], bp[1], bp[2], bp[3], ap[0], ap[1], ap[2], ap[3], bt, at, "");
	}
	
	private static String getPos(Element sent_node, int id) {
		Element word_node = getWord_nodeNyId(sent_node, id);
		if (word_node == null)
			return "null";
		else
			return word_node.attribute("pos").getText();
	}
	
	private static Element getWord_nodeNyId(Element sent_node, int id) {
		for(Iterator it_word = sent_node.elementIterator(); it_word.hasNext();){
			Element word_node = (Element) it_word.next();
			if (word_node.attribute("id").getText().equals(Integer.toString(id)))
				return word_node;
		}
		return null;
	}
	
	private static String[] getType(Element sent_node, int id) {
		ArrayList<String> types = new ArrayList<String>();
		String[] output = new String[2];
		for (int i = 0; getWord_nodeNyId(sent_node, i) != null; i++)
			types.add(getWord_nodeNyId(sent_node, i).attribute("ne").getText());
		int index = id;
		for (int i = id; getWord_nodeNyId(sent_node, i) != null; i--){
			if (!getWord_nodeNyId(sent_node, i).attribute("ne").getText().equals("O"))
				index = i;
		}
		if (index == id)
			output[0] = "null";
		else
			output[0] = getWord_nodeNyId(sent_node, index).attribute("ne").getText();
		index = id;
		for (int i = id; getWord_nodeNyId(sent_node, i) != null; i++){
			if (!getWord_nodeNyId(sent_node, i).attribute("ne").getText().equals("O"))
				index = i;
		}
		if (index == id)
			output[1] = "null";
		else
			output[1] = getWord_nodeNyId(sent_node, index).attribute("ne").getText();
		return output;
	}

}
