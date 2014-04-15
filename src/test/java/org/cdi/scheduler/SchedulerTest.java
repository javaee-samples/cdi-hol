package org.cdi.scheduler;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

/**
 * @author Antoine Sabot-Durand
 */
@RunWith(Arquillian.class)
public class SchedulerTest {

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {
        
        JavaArchive[] libs = Maven.resolver()
                       .loadPomFromFile("pom.xml")
                       .resolve("org.quartz-scheduler:quartz")
                       .withTransitivity().as(JavaArchive.class);

        WebArchive ret = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addAsLibraries(libs)
                .addClasses(TestJob.class,
                        QuartzScheduler.class,
                        Scheduled.class,
                        Scheduler.class,
                        SchedulerExtension.class,
                        SchedulerProducer.class,
                        TestService.class)
                .addAsServiceProvider(Extension.class, SchedulerExtension.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return ret;
    }

    @Inject
    Scheduler scheduler;
    
    @Inject
    TestService service;
   
    @Test
    public void schedulerShouldContainJob() throws InterruptedException {
        Thread.sleep(2000);
        Assert.assertTrue(service.getValue() > 0); 
    }

}
