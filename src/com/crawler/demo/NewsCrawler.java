package com.crawler.demo;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.nodes.Document;

/**
 * Crawling news from hfut news
 *
 * @author hu
 */
public class NewsCrawler extends BreadthCrawler {

    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     * information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     * links which match regex rules from pag
     */
	
	private final int page = 15;
	
    public NewsCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        /*种子页面*/
        this.addSeed("http://china.chinadaily.com.cn/node_1143902.htm");
        for (int i = 2; i <= page; i++)
        	this.addSeed("http://china.chinadaily.com.cn/node_1143902_" + i + ".htm");
        /*正则规则设置*/
        /*爬取符合 http://news.hfut.edu.cn/show-xxxxxxhtml的URL*/
        this.addRegex("http://china.chinadaily.com.cn/.*/content_.*.htm");
        /*不要爬取 jpg|png|gif*/
        this.addRegex("-.*\\.(jpg|png|gif).*");
        /*不要爬取包含 # 的URL*/
        this.addRegex("-.*#.*");
    }

    @Override
    public void visit(Page page, CrawlDatums next)  {
        String url = page.getUrl();
        /*判断是否为新闻页，通过正则可以轻松判断*/
        if (page.matchUrl("http://china.chinadaily.com.cn/.*/content_.*.htm")) {
            /*we use jsoup to parse page*/
            Document doc = page.getDoc();

            /*extract title and content of news by css selector*/
            String title = page.select("div[class=container-left2]>h1").text();
            String content = null;
            try{
            	content = page.select("div[id=Content]", 0).text();
            } catch (IndexOutOfBoundsException e){
            	content = page.select("div[class=arcBox]", 0).text();
            }

            File outputfile = new File("text//" + url.substring(url.indexOf("content") + 8, url.length() - 4) + ".txt");
            try {
                if (outputfile.exists())
                	outputfile.delete();
				outputfile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile, true));
				bw.write(content);
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            /*如果你想添加新的爬取任务，可以向next中添加爬取任务，
               这就是上文中提到的手动解析*/
            /*WebCollector会自动去掉重复的任务(通过任务的key，默认是URL)，
              因此在编写爬虫时不需要考虑去重问题，加入重复的URL不会导致重复爬取*/
            /*如果autoParse是true(构造函数的第二个参数)，爬虫会自动抽取网页中符合正则规则的URL，
              作为后续任务，当然，爬虫会去掉重复的URL，不会爬取历史中爬取过的URL。
              autoParse为true即开启自动解析机制*/
            //next.add("http://xxxxxx.com");
        }
    }

    public static void main(String[] args) throws Exception {
        NewsCrawler crawler = new NewsCrawler("crawl", true);
        /*线程数*/
        crawler.setThreads(50);
        /*设置每次迭代中爬取数量的上限*/
        crawler.setTopN(5000);
        /*设置是否为断点爬取，如果设置为false，任务启动前会清空历史数据。
           如果设置为true，会在已有crawlPath(构造函数的第一个参数)的基础上继
           续爬取。对于耗时较长的任务，很可能需要中途中断爬虫，也有可能遇到
           死机、断电等异常情况，使用断点爬取模式，可以保证爬虫不受这些因素
           的影响，爬虫可以在人为中断、死机、断电等情况出现后，继续以前的任务
           进行爬取。断点爬取默认为false*/
        //crawler.setResumable(true);
        /*开始深度为4的爬取，这里深度和网站的拓扑结构没有任何关系
            可以将深度理解为迭代次数，往往迭代次数越多，爬取的数据越多*/
        crawler.start(4);
    }

}