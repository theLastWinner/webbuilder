package org.webbuilder.utils.quartz;

import org.junit.Test;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by 浩 on 2015-11-03 0003.
 */
public class TestQuartz {

    @Test
    public void testQuartz() throws Exception {
        //1秒执行一次
        String cron = "*/1 * * * * ?";

        QuartzBuilder.addJob(new QuartzJob("haha", "g1", cron, new QuartzJob.JobExecute() {
            public <T> T execute(JobExecutionContext arg0) throws JobExecutionException {
                return (T) "11";
            }
        }));

        QuartzBuilder.addJob(new QuartzJob("hehe", "g1", "*/1 * * * * ?", new QuartzJob.JobExecute() {
            public <T> T execute(JobExecutionContext arg0) throws JobExecutionException {
                return (T) "aa";
            }
        }));

        Thread.sleep(5000);
        QuartzBuilder.pauseJob("hehe", "g1");//暂停
        Thread.sleep(5000);
        QuartzBuilder.resumeJob("hehe", "g1");//唤醒
        Thread.sleep(5000);
        QuartzBuilder.deleteJob("hehe", "g1");//删除
    }
}
