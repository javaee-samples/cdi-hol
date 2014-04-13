package org.cdi.extensions;

import java.util.HashMap;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * @author Antoine Sabot-Durand
 */
public class HashMapAsBeanExtension implements Extension{
    
    public void addHashMapAsAnnotatedType(@Observes BeforeBeanDiscovery bbd,BeanManager beanManager)
    {
        bbd.addAnnotatedType(beanManager.createAnnotatedType(HashMap.class));
    }
    
    
}
