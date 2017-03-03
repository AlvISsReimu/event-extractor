package com.eventextracting.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class XmlPassage {
	
	private static ArrayList<String> xmls = new ArrayList<String>();
	
	public static ArrayList<String> getXmls(String passage, String filename) {
		File file = new File("xml//" + filename.substring(0, filename.length() - 4));
		ArrayList<String> newxmls = null;
		if (!file.exists() && !file.isDirectory()){
			newxmls = xmlSentences(splitPassage(passage));
			try {
				file.mkdir();
				for (int i = 0; i < newxmls.size(); i++){
					File xmlf = new File("xml//" + filename.substring(0, filename.length() - 4) + "//" + i + ".xml");
					xmlf.createNewFile();
					BufferedWriter bw = new BufferedWriter(new FileWriter(xmlf, true));
					bw.write(newxmls.get(i));
					bw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			System.out.println(filename + "已存在xml文件。");
			File[] filelist = file.listFiles();
			List<File> xmlflist = new ArrayList<File>();
			for (File xmlf: filelist){
				if (xmlf.isFile() && xmlf.exists() && xmlf.getName().endsWith(".xml"))
					xmlflist.add(xmlf);
			}
			newxmls = readXmlsFromXmlFileList(xmlflist);
		}
		xmls.addAll(newxmls);
		return newxmls;
	}
	
	public static ArrayList<String> getXmls() {
		return xmls;
	}
	
	private static String[] splitPassage(String passage) {
		String[] sentences = passage.split("。");
		for (int i = 0; i < sentences.length; i++){
			sentences[i] += "。";
			sentences[i].replaceAll("\n", "");
		}
		System.out.println("文章分解得到" + sentences.length + "个句子。");
		return sentences;
	}
	
	private static ArrayList<String> xmlSentences(String[] sentences) {
		ArrayList<String> newxmls = new ArrayList<String>();
		String api_key = "k6O7q7s57EzAJulLKUoh8g6EZFmIbtDxRTQwcNzm";
        String pattern = "all";
        String format  = "xml";
        System.out.println("开始应用语言云进行句法分析。");
        int finished = 0;
        int block = 0;
        for (String text: sentences){
        	try {
        		String encoded = URLEncoder.encode(text, "utf-8");
        		for (int l = encoded.length(); l < 383; l++)
        			text += " ";
        		encoded = URLEncoder.encode(text, "utf-8");
				URL url = new URL("http://api.ltp-cloud.com/analysis/?"
	                      + "api_key=" + api_key + "&"
	                      + "text="    + encoded + "&"
	                      + "format="  + format  + "&"
	                      + "pattern=" + pattern);
				URLConnection conn = url.openConnection();
				conn.connect();
	            BufferedReader innet = new BufferedReader(new InputStreamReader(
	            		conn.getInputStream(), "utf-8"));
	            String line;
	            String xml = "";
	            while ((line = innet.readLine())!= null)
	            	xml += line;
	            innet.close();
	            xmls.add(xml);
	            newxmls.add(xml);
	            finished++;
	            int temp_block = 10 * finished / sentences.length;
	            for (int i = 0; i < (temp_block - block); i++)
	            	System.out.print("■");
	            block = temp_block;
			}
        	catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        System.out.println("\n句法分析成功。");
        return newxmls;
	}
	
	/*
	private static void printRate(int total, int finished) {
		if (total == 0)
			return ;
		int digit = Integer.toString(total).length();
		if (finished != 0){
			int back = (1 + 2 * digit) * 2;
			for (int i = 0; i < back ; i++)
				System.out.print("\b");
		}
		System.out.printf("%" + digit + "d", 0);
		System.out.print("/" + total);
	}*/
	
	public static ArrayList<String> readXmlsFromXmlFileList(List<File> xmlflist) {
		ArrayList<String> output = new ArrayList<String>();
		for (int i = 0; i < xmlflist.size(); i++){
			try{
				File xmlf = xmlflist.get(i);
				InputStreamReader read = new InputStreamReader(new FileInputStream(xmlf), "utf-8");
		    	BufferedReader bufferedReader = new BufferedReader(read);
		    	String xml = "";
		    	String s = null;
		    	while((s = bufferedReader.readLine()) != null)
		    		xml += s;
		    	output.add(xml);
		    	read.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		xmls = output;
		return output;
	}
}
