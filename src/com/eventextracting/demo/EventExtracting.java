package com.eventextracting.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;

import com.eventextracting.bean.ClassifiedWords;
import com.eventextracting.bean.EventExtWord;
import com.eventextracting.bean.Features;
import com.eventextracting.bean.Trigger;

import javafx.util.Pair;

public class EventExtracting {

	public static void main(String[] args) throws IOException, DocumentException {
		File[] filelist = (new File("text")).listFiles();
		List<File> textlist = new ArrayList<File>();
		for (File file: filelist){
			if (file.isFile() && file.exists() && file.getName().endsWith(".txt"))
				textlist.add(file);
		}
		System.out.println("text目录下已检测到" + textlist.size() + "个.txt文件");
		
		for (int i = 0; i < textlist.size(); i++){
			File file = textlist.get(i);
			String text = GenerateDict.extStringFromFile(file);
	    	
	    	System.out.println("开始处理" + file.getName());
			TriggerExtracting.extTrigger(text, file.getName(), false);
			System.out.println(file.getName() + "处理完毕。剩余" + (textlist.size() - i - 1) + "个文件。");
		}
		
		ArrayList<Trigger> triggers = TriggerExtracting.getTriggers();
		System.out.println("共抽取" + triggers.size() + "个触发词。");
		
		ArrayList<EventExtWord> elements = TriggerExtracting.getSubobjs();
        MaxEnt maxEnt = new MaxEnt();
        System.out.println("装载学习数据。");
        maxEnt.loadData("train.txt");
        System.out.println("开始机器学习。");
        maxEnt.train(500);
        System.out.println("机器学习结束。\n");
        MachineLearning.readDict(new File("dict.txt"));
		for (EventExtWord subobj: elements){
			String dictresult = getCodeByDict(subobj.trigger);
			if (!dictresult.equals("null")){
				List<String> fieldList = subobj.features.genFieldList();
				String MEresult = MaxEnt.getMaxPType(MaxEnt.ME(maxEnt, fieldList));
				System.out.println(subobj.sub + " " + subobj.trigger + " " + subobj.obj);
				System.out.println("Dict: " + dictresult);
				System.out.println("ME: " + MEresult);
				if (MEresult.startsWith(dictresult))
					System.out.println("一致");
				else
					System.out.println("不同");
				System.out.print("\n");
			}
		}
		
		System.exit(0);
	}
	
	private static String getCodeByDict(String word) {
		ArrayList<ClassifiedWords> dict = MachineLearning.getDict();
		for (ClassifiedWords cw: dict){
			if (cw.getWords().contains(word))
				return cw.getType();
		}
		return "null";
	}
}
