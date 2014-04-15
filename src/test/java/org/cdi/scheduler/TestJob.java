package org.cdi.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.enterprise.inject.spi.CDI;

/**
 * @author Antoine Sabot-Durand
 */
@Scheduled(cronExpression = "0/1 * * * * ?")
public class TestJob implements Job {
    
    
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        TestService service = null;
        try {
            service = CDI.current().select(TestService.class).get();
        } catch (Exception e) {
            return;
        }

        service.increment();
        
    }
}
