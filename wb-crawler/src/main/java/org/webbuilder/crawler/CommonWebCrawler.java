package org.webbuilder.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.webbuilder.crawler.extracter.DefaultHtmlContentExtractor;
import org.webbuilder.crawler.extracter.HtmlContentExtractor;
import org.webbuilder.crawler.listener.CrawlerListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by 浩 on 2015-09-06 0006.
 */
public class CommonWebCrawler extends WebCrawler {

    /**
     * url正则匹配
     */
    private Pattern urlPattern;

    /**
     * url 路径匹配，支持如: http://host/*
     */
    private List<String> pathMatcher = new ArrayList<>();

    private final PathMatcher matcher = new AntPathMatcher();

    private int waitFor = 0;

    private static final HtmlContentExtractor defaultExtractor = new DefaultHtmlContentExtractor();

    private HtmlContentExtractor extractor;

    protected List<CrawlerListener> listeners = new LinkedList<>();

    public void addListener(CrawlerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CrawlerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CommonWebCrawler crawler = new CommonWebCrawler();
        crawler.setExtractor(getExtractor());
        crawler.setUrlPattern(getUrlPattern());
        crawler.setWaitFor(getWaitFor());
        crawler.setPathMatcher(getPathMatcher());
        crawler.listeners = this.listeners;
        return crawler;
    }

    @Override
    public boolean shouldVisit(Page page, WebURL url) {
        try {
            Thread.sleep(waitFor);
        } catch (InterruptedException e) {
        }
        boolean shouldVisit = true;
        try {
            String url_str = url.toString();
            if (urlPattern != null && urlPattern.matcher(url_str).matches()) {
                return shouldVisit = true;
            }
            if (pathMatcher != null) {
                for (int i = 0, len = pathMatcher.size(); i < len; i++) {
                    if (matcher.match(pathMatcher.get(i), url_str)) {
                        return shouldVisit = true;
                    }
                }
            }
            return shouldVisit = (urlPattern == null && pathMatcher == null);
        } finally {
            for (CrawlerListener listener : listeners) {
                if (!listener.onShouldVisit(page, url, shouldVisit)) {
                    return false;
                }
            }
        }
    }

    @Override
    public void visit(Page page) {
        String content = "";
        try {
            if (logger.isInfoEnabled())
                logger.info(page.getWebURL().toString());
            //html内容
            String html = new String(page.getContentData(), page.getContentCharset());
            //提取正文
            content = getExtractor().parse(html);
        } catch (Exception e) {
            logger.error(String.format("visit page %s error", page.getWebURL()), e);
            for (CrawlerListener listener : listeners) {
                listener.onError(page, e);
            }
        } finally {
            for (CrawlerListener listener : listeners) {
                listener.onVisit(page, content);
            }
        }
    }

    public Pattern getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(Pattern urlPattern) {
        this.urlPattern = urlPattern;
    }

    public int getWaitFor() {
        return waitFor;
    }

    public void setWaitFor(int waitFor) {
        this.waitFor = waitFor;
    }

    public HtmlContentExtractor getExtractor() {
        if (extractor == null) extractor = defaultExtractor;
        return extractor;
    }

    public void setExtractor(HtmlContentExtractor extractor) {
        this.extractor = extractor;
    }

    public List<String> getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(List<String> pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * 设置路径过滤条件，符合过滤条件的，将不进行抓取(也不会抓取其中的链接)
     *
     * @param pathMatcher 过滤条件如: http://www.host.com/**.html
     */
    public void addPathMatcher(String pathMatcher) {
        getPathMatcher().add(pathMatcher);
    }
}
