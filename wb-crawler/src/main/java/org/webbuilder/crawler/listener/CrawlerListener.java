package org.webbuilder.crawler.listener;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Created by 浩 on 2015-11-04 0004.
 */
public interface CrawlerListener {
    boolean onShouldVisit(Page page, WebURL url, boolean shouldVisit);

    void onVisit(Page page, String content);

    void onError(Page page, Throwable e);
}
