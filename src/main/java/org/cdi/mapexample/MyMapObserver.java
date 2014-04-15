package org.cdi.mapexample;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;

/**
 * @author Antoine Sabot-Durand
 */
@ApplicationScoped
@Default
public class MyMapObserver {
    
    public void observeMapAddition(@Observes @AddedToMap String msg) {
        System.out.println("-------  Received addition event:" +msg);
    }
}
