package com.eventextracting.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.eventextracting.bean.ClassifiedWords;
import com.eventextracting.bean.Trigger;
import com.eventextracting.bean.TriggerCluster;

public class GenerateDict {

	public static void main(String[] args) throws Exception {
		WordSimilarity.readCiLin();
		
		File triggersfile = new File("save//triggers.txt");
		if (triggersfile.exists())
			System.out.println("已读入" + TriggerExtracting.readTriggers(triggersfile) + "个触发词。");
		else
			triggersfile.createNewFile();
		File[] filelist = (new File("text")).listFiles();
		List<File> textlist = new ArrayList<File>();
		for (File file: filelist){
			if (file.isFile() && file.exists() && file.getName().endsWith(".txt"))
				textlist.add(file);
		}
		System.out.println("text目录下已检测到" + textlist.size() + "个.txt文件");
		
		for (int i = 0; i < textlist.size(); i++){
			File file = textlist.get(i);
			String text = extStringFromFile(file);
	    	
	    	System.out.println("开始处理" + file.getName());
			TriggerExtracting.extTrigger(text, file.getName(), true);
			System.out.println(file.getName() + "处理完毕。剩余" + (textlist.size() - i - 1) + "个文件。");
			
            File outputfile = new File("save//triggers.txt");
            TriggerExtracting.saveTriggers(outputfile);
            
            copyFile("text//" + file.getName(), "text//finished//" + file.getName());
            delFile("text//" + file.getName());
		}
		
		ArrayList<Trigger> triggers = TriggerExtracting.getTriggers();
		System.out.println("共抽取" + triggers.size() + "个触发词。");
		
		
		TriggerClustering.clusTriggers(triggers);
		ArrayList<TriggerCluster> clusters = TriggerClustering.getClusters();
		System.out.println("共获得" + clusters.size() + "个触发词类。");
        File outputfile = new File("save//clusters.txt");
        TriggerClustering.saveClusters(outputfile);
        
		
		//TriggerClustering.readClusters(new File("save//clusters.txt"));
		
		ArrayList<ClassifiedWords> dict = SynExpand.expSyn(TriggerClustering.getClusters());
		for (ClassifiedWords cw: dict){
			System.out.println(cw);
		}
		
		//MachineLearning.Learning();
		
        System.exit(0);
	}
	
	 /**  
     *  复制单个文件  
     *  @param  oldPath  String  原文件路径  如：c:/fqf.txt  
     *  @param  newPath  String  复制后路径  如：f:/fqf.txt  
     *  @return  boolean  
     */  
	public static void copyFile(String oldPath, String newPath) {
		try{
			int  byteread  =  0;
			File  oldfile  =  new  File(oldPath);
			if  (oldfile.exists())  {  //文件存在时 
				InputStream inStream = new FileInputStream(oldPath);  //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while((byteread = inStream.read(buffer)) != -1)
					fs.write(buffer,  0,  byteread);
				inStream.close();
			}
		} catch (Exception e){
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}
	
	 /**  
     *  删除文件  
     *  @param  filePathAndName  String  文件路径及名称  如c:/fqf.txt  
     *  @param  fileContent  String  
     *  @return  boolean  
     */  
	public static void delFile(String filePathAndName) {
       try {
    	   String filePath = filePathAndName;
           filePath = filePath.toString();
           File myDelFile = new File(filePath);
           myDelFile.delete();
       }
       catch (Exception e){
           System.out.println("删除文件操作出错");
           e.printStackTrace();
       }
   }  
	
	public static String extStringFromFile(File file) throws IOException {
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
    	BufferedReader bufferedReader = new BufferedReader(read);
    	String text = "";
    	String s = null;
    	while((s = bufferedReader.readLine()) != null)
    		text += s;
    	read.close();
    	return text;
	}
	
}