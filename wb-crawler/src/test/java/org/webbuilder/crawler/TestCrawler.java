package org.webbuilder.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.webbuilder.crawler.listener.CrawlerListener;

import java.util.Date;

/**
 * Created by 浩 on 2015-11-04 0004.
 */
public class TestCrawler {

    static int counting = 0;
    static final String url = "http://202.98.57.19:9006/solr/";
    static final SolrClient client = new HttpSolrClient(url);
    static final PathMatcher matcher = new AntPathMatcher();

    public static void main(String[] args) throws Exception {
        start();

    }


    public static void start() throws Exception {
        String crawlStorageFolder = "/crawler/data/root";
        int numberOfCrawlers = 2;//爬虫数量
        CrawlConfig config = new CrawlConfig();
        config.setConnectionTimeout(10 * 1000);
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxTotalConnections(10000);
        config.setMaxConnectionsPerHost(10000);
        config.setResumableCrawling(true);
        config.setMaxDepthOfCrawling(5);//爬行深度
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        config.setUserAgentString("Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        controller.addSeed("http://www.jd.com/");
        CommonWebCrawler crawler = new CommonWebCrawler();
        // crawler.setExtractor(new JsoupHtmlContentExtractor(".content"));
        crawler.setWaitFor(1000);

        crawler.addPathMatcher("http://*.jd.com/**/*");
        crawler.addListener(new CrawlerListener() {

            @Override
            public boolean onShouldVisit(Page page, WebURL url, boolean shouldVisit) {
                return shouldVisit;
            }

            @Override
            public void onVisit(Page page, String content) {
                try {
                    if (!matcher.match("http://item.jd.com/*.html", page.getWebURL().toString())) {
                        System.out.println(page.getWebURL() + " 跳过!"+page.getWebURL().getDomain());
                        return;
                    }
                    SolrInputDocument document = new SolrInputDocument();
                    String html = new String(page.getContentData(), page.getContentCharset());
                    document.addField("content_s", content);
                    document.addField("url_s", page.getWebURL().toString());
                    document.addField("title_s", Jsoup.parse(html).select("title").text());
                    document.addField("create_date_d", new Date());
                    document.addField("id", String.valueOf(page.getWebURL().getDocid()));
                    client.add("main", document);
                    client.commit("main");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                counting++;
                System.out.println("已抓取内容 " + page.getWebURL().getDocid() + ":" + page.getWebURL() + " 累计抓取" + counting + "个页面,当前深度" + page.getWebURL().getDepth());
            }

            @Override
            public void onError(Page page, Throwable e) {
                e.printStackTrace();
            }
        });

        controller.start(crawler, numberOfCrawlers, false);
    }

}
