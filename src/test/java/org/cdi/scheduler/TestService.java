package org.cdi.scheduler;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Antoine Sabot-Durand
 */
@ApplicationScoped
public class TestService {

   

    private int value=0;
    
    public void increment() {
        System.out.println("******  Incrementing ******");
        value++;
    }
    
    public int getValue() {
           return value;
       }
}
