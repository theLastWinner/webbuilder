package org.webbuilder.utils.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzBuilder.class);

    private static final SchedulerFactory sf = new StdSchedulerFactory();

    private static Scheduler sched = null;

    static {
        try {
            sched = sf.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private QuartzBuilder() {

    }

    public static void addJob(QuartzJob job) throws Exception {

        LOGGER.info("add QuartzJob:" + job.getGroup() + "." + job.getName() + "-->" + job.getExecutor().getClass() + " ,cron=" + job.getCron());
        // cron表达式
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(job.getCron());
        JobDetail job_ = JobBuilder.newJob(job.getClass()).withIdentity(job.getName(), job.getGroup()).build();

        // 将原始信息动态传递到任务中，进行初始化
        job_.getJobDataMap().put("originalJob", job);

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger_" + job.getName(), job.getGroup()).withSchedule(builder).build();
        sched.scheduleJob(job_, trigger);
        sched.start();
        LOGGER.info("QuartzJob:" + job.getGroup() + "." + job.getName() + "-->" + job.getExecutor().getClass() + " started!");

    }

    public static void deleteJob(QuartzJob job) throws Exception {
        deleteJob(job.getName(), job.getGroup());
    }

    public static void deleteJob(String name, String group) throws Exception {
        LOGGER.info("deleteJob:" + group + "." + name);
        if (getJob(name, group) != null)
            sched.deleteJob(new JobKey(name, group));
    }

    public static JobDetail getJob(String name, String group) throws Exception {
        return sched.getJobDetail(new JobKey(name, group));
    }

    public static void deleteJob(String name) throws Exception {
        LOGGER.info("deleteJob:" + "?." + name);
        sched.deleteJob(new JobKey(name));
    }

    public static void pauseJob(QuartzJob job) throws Exception {
        pauseJob(job.getName(), job.getGroup());
    }

    public static void pauseJob() throws Exception {
        sched.pauseAll();
    }

    public static void pauseJob(String name, String group) throws Exception {
        sched.pauseJob(new JobKey(name, group));
    }

    public static void pauseJob(String name) throws Exception {
        LOGGER.info("pauseJob:" + "?." + name);
        sched.pauseJob(new JobKey(name));
    }

    public static void resumeJob(QuartzJob job) throws Exception {
        resumeJob(job.getName(), job.getGroup());
    }

    public static void resumeJob(String name, String group) throws Exception {
        LOGGER.info("resumeJob:" + group + "." + name);
        sched.resumeJob(new JobKey(name, group));
    }

    public static void resumeJob(String name) throws Exception {
        LOGGER.info("resumeJob:" + "?." + name);
        sched.resumeJob(new JobKey(name));
    }

    public static void resumeJob() throws Exception {
        sched.resumeAll();
    }
}
