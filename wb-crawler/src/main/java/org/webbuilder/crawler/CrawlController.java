/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.webbuilder.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.crawler.listener.CrawlerJobListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CrawlController extends edu.uci.ics.crawler4j.crawler.CrawlController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected List<CrawlerJobListener> crawlerJobListeners = new LinkedList<>();

    public CrawlController(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer) throws Exception {
        super(config, pageFetcher, robotstxtServer);
    }

    public <T extends CommonWebCrawler> void start(final T crawler_proto, int numberOfCrawlers, boolean isBlocking) {
        try {
            finished = false;
            crawlersLocalData.clear();
            final List<Thread> threads = new ArrayList<>();
            final List<T> crawlers = new ArrayList<>();
            T crawler = (T) crawler_proto.clone();
            for (int i = 1; i <= numberOfCrawlers; i++) {
                Thread thread = new Thread(crawler, "Crawler " + i);
                crawler.setThread(thread);
                crawler.init(i, this);
                thread.start();
                crawlers.add(crawler);
                threads.add(thread);
                logger.info("Crawler {} started", i);
            }

            final CrawlController controller = this;

            Thread monitorThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        synchronized (waitingLock) {
                            while (true) {
                                sleep(10);
                                boolean someoneIsWorking = false;
                                for (int i = 0; i < threads.size(); i++) {
                                    Thread thread = threads.get(i);
                                    if (!thread.isAlive()) {
                                        if (!shuttingDown) {
                                            logger.info("Thread {} was dead, I'll recreate it", i);
                                            T crawler = (T) crawler_proto.clone();
                                            thread = new Thread(crawler, "Crawler " + (i + 1));
                                            threads.remove(i);
                                            threads.add(i, thread);
                                            crawler.setThread(thread);
                                            crawler.init(i + 1, controller);
                                            thread.start();
                                            crawlers.remove(i);
                                            crawlers.add(i, crawler);
                                        }
                                    } else if (crawlers.get(i).isNotWaitingForNewURLs()) {
                                        someoneIsWorking = true;
                                    }
                                }
                                if (!someoneIsWorking) {
                                    // Make sure again that none of the threads are alive.
                                    logger.info("It looks like no thread is working, waiting for 10 seconds to make sure...");
                                    sleep(10);
                                    someoneIsWorking = false;
                                    for (int i = 0; i < threads.size(); i++) {
                                        Thread thread = threads.get(i);
                                        if (thread.isAlive() && crawlers.get(i).isNotWaitingForNewURLs()) {
                                            someoneIsWorking = true;
                                        }
                                    }
                                    if (!someoneIsWorking) {
                                        if (!shuttingDown) {
                                            long queueLength = frontier.getQueueLength();
                                            if (queueLength > 0) {
                                                continue;
                                            }
                                            logger.info("No thread is working and no more URLs are in queue waiting for another 10 seconds to make sure...");
                                            sleep(10);
                                            queueLength = frontier.getQueueLength();
                                            if (queueLength > 0) {
                                                continue;
                                            }
                                        }

                                        logger.info("All of the crawlers are stopped. Finishing the process...");
                                        // At this step, frontier notifies the threads that were waiting for new URLs and they should stop
                                        frontier.finish();
                                        for (T crawler : crawlers) {
                                            crawler.onBeforeExit();
                                            crawlersLocalData.add(crawler.getMyLocalData());
                                        }

                                        logger.info("Waiting for 10 seconds before final clean up...");
                                        sleep(10);
                                        frontier.close();
                                        docIdServer.close();
                                        pageFetcher.shutDown();
                                        finished = true;
                                        waitingLock.notifyAll();
                                        env.close();
                                        for (CrawlerJobListener crawlerJobListener : crawlerJobListeners) {
                                            crawlerJobListener.onEnd();
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Unexpected Error", e);
                    }
                }
            });

            monitorThread.start();
            for (CrawlerJobListener crawlerJobListener : crawlerJobListeners) {
                crawlerJobListener.onStart();
            }
            if (isBlocking) {
                waitUntilFinish();
            }

        } catch (Exception e) {
            logger.error("Error happened", e);
        }
    }


    public void addListener(CrawlerJobListener listener) {
        this.crawlerJobListeners.add(listener);
    }

    public void removeListener(CrawlerJobListener listener) {
        this.crawlerJobListeners.remove(listener);
    }
}