/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.cdi.scheduler;


import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.inject.Vetoed;

@Vetoed
public class QuartzScheduler implements Scheduler<Job> {
    protected org.quartz.Scheduler scheduler;

    @Override
    public void start()  {

        SchedulerFactory schedulerFactory;
        schedulerFactory = new StdSchedulerFactory();
      
        try {
            this.scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
           e.printStackTrace();
        }
        
       


    }

    @Override
    public void stop() {
        try {
                this.scheduler.shutdown(true);
                this.scheduler = null;
            }
         catch (SchedulerException e) {
             e.printStackTrace();
        }
    }

    @Override
    public void registerNewJob(Class<? extends Job> jobClass) {
        JobKey jobKey = createJobKey(jobClass);

        try {
            Scheduled scheduled = jobClass.getAnnotation(Scheduled.class);

            String description = scheduled.description();

            if ("".equals(scheduled.description())) {
                description = jobClass.getName();
            }

            JobDetail jobDetail = this.scheduler.getJobDetail(jobKey);
            Trigger trigger;

            if (jobDetail == null) {
                jobDetail = JobBuilder.newJob(jobClass)
                        .withDescription(description)
                        .withIdentity(jobKey)
                        .build();

                trigger = TriggerBuilder.newTrigger()
                        .withSchedule(CronScheduleBuilder.cronSchedule(scheduled.cronExpression()))
                        .build();

                this.scheduler.scheduleJob(jobDetail, trigger);
            } else if (scheduled.overrideOnStartup()) {
                List<? extends Trigger> existingTriggers = this.scheduler.getTriggersOfJob(jobKey);

                if (existingTriggers == null || existingTriggers.isEmpty()) {
                    //TODO re-visit it
                    trigger = TriggerBuilder.newTrigger()
                            .withSchedule(CronScheduleBuilder.cronSchedule(scheduled.cronExpression()))
                            .build();

                    this.scheduler.scheduleJob(jobDetail, trigger);
                    return;
                }

                if (existingTriggers.size() > 1) {
                    throw new IllegalStateException("multiple triggers found for " + jobKey + " ('" + jobDetail + "')" +
                            ", but aren't supported by @" + Scheduled.class.getName() + "#overrideOnStartup");
                }

                trigger = existingTriggers.iterator().next();

                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(trigger.getKey())
                        .withSchedule(CronScheduleBuilder.cronSchedule(scheduled.cronExpression()))
                        .build();

                this.scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else {
                Logger.getLogger(QuartzScheduler.class.getName()).info(jobKey + " exists already and will be ignored.");
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startJobManually(Class<? extends Job> jobClass) {
        try {
            this.scheduler.triggerJob(createJobKey(jobClass));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interruptJob(Class<? extends Job> jobClass) {
        try {
            this.scheduler.interrupt(createJobKey(jobClass));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseJob(Class<? extends Job> jobClass) {
        try {
            this.scheduler.pauseJob(createJobKey(jobClass));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resumeJob(Class<? extends Job> jobClass) {
        try {
            this.scheduler.resumeJob(createJobKey(jobClass));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isExecutingJob(Class<? extends Job> jobClass) {
        try {
            JobKey jobKey = createJobKey(jobClass);
            JobDetail jobDetail = this.scheduler.getJobDetail(jobKey);

            if (jobDetail == null) {
                return false;
            }

            for (JobExecutionContext jobExecutionContext : this.scheduler.getCurrentlyExecutingJobs()) {
                if (jobKey.equals(jobExecutionContext.getJobDetail().getKey())) {
                    return true;
                }
            }

            return false;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static JobKey createJobKey(Class<?> jobClass) {
        Scheduled scheduled = jobClass.getAnnotation(Scheduled.class);

        if (scheduled == null) {
            throw new IllegalStateException("@" + Scheduled.class.getName() + " is missing on " + jobClass.getName());
        }

        String groupName = scheduled.group().getSimpleName();
        String jobName = jobClass.getSimpleName();

        if (!Scheduled.class.getSimpleName().equals(groupName)) {
            return new JobKey(jobName, groupName);
        }
        return new JobKey(jobName);
    }


    @Override
    public <S> S unwrap(Class<? extends S> schedulerClass) {
        if (schedulerClass.isAssignableFrom(this.scheduler.getClass())) {
            return (S) this.scheduler;
        }

        throw new IllegalArgumentException(schedulerClass.getName() +
                " isn't compatible with " + this.scheduler.getClass().getName());
    }
}
