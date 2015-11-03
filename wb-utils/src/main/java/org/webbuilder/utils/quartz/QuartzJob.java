package org.webbuilder.utils.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.Serializable;

/**
 * 对Quartz-Job的简单封装，使其能支持内部类调用
 *
 * @author zhouhao
 */
public class QuartzJob implements Job {

    /**
     * 任务执行接口
     */
    private JobExecute executor;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务分组
     */
    private String group;

    private byte status;

    private boolean executed;

    public QuartzJob() {

    }

    public QuartzJob(String jobName, String group, String cron, JobExecute execuor) {
        setName(jobName);
        setGroup(group);
        setCron(cron);
        setExecutor(execuor);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public JobExecute getExecutor() {
        return executor;
    }

    public void setExecutor(JobExecute executor) {
        this.executor = executor;
    }

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        JobDataMap dataMap = arg0.getJobDetail().getJobDataMap();

        if (getExecutor() == null) {
            // 初始化操作
            Object originalJob_ = dataMap.get("originalJob");
            if (originalJob_ != null && originalJob_ instanceof QuartzJob) {
                QuartzJob originalJob = (QuartzJob) originalJob_;
                this.setExecutor(originalJob.getExecutor());
                this.setGroup(originalJob.getGroup());
                this.setName(originalJob.getName());
            }
        }
        // 执行任务
        if (getExecutor() != null)
            getExecutor().execute(arg0);

        this.executed = true;
    }

    public interface JobExecute extends Serializable {
        /**
         * 任务开始时被调用
         *
         * @param arg0
         * @throws JobExecutionException
         */
        <T> T execute(JobExecutionContext arg0) throws JobExecutionException;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public boolean isExecuted() {
        return executed;
    }

}
