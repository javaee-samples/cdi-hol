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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

public class SchedulerExtension implements Extension {


    private List<Class> foundManagedJobClasses = new ArrayList<>();

    private Scheduler scheduler;


    public <X> void findScheduledJobs(@Observes @WithAnnotations({ Scheduled.class }) ProcessAnnotatedType<X> pat) {

        Class<X> beanClass = pat.getAnnotatedType().getJavaClass();

        Scheduled scheduled = pat.getAnnotatedType().getAnnotation(Scheduled.class);
        if (scheduled != null && scheduled.onStartup()) {
            this.foundManagedJobClasses.add(beanClass);
        }
    }

    public <X> void scheduleJobs(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {

        initScheduler(afterBeanDiscovery);


        List<String> foundJobNames = new ArrayList<String>();

        for (Class jobClass : this.foundManagedJobClasses) {
            if (foundJobNames.contains(jobClass.getSimpleName())) {
                afterBeanDiscovery.addDefinitionError(
                        new IllegalStateException("Multiple Job-Classes found with name " + jobClass.getSimpleName()));
            }

            foundJobNames.add(jobClass.getSimpleName());
            this.scheduler.registerNewJob(jobClass);
        }
    }

    public <X> void stopScheduler(@Observes BeforeShutdown beforeShutdown) {

        if (this.scheduler != null) {
            this.scheduler.stop();
            this.scheduler = null;
        }
    }

    private void initScheduler(AfterBeanDiscovery afterBeanDiscovery) {

        this.scheduler = new QuartzScheduler();

        try {
            this.scheduler.start();
        } catch (Throwable t) {
            afterBeanDiscovery.addDefinitionError(t);
        }

    }


    Scheduler getScheduler() {
        return scheduler;
    }
}
