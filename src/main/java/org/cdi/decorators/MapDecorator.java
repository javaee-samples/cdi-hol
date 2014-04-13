package org.cdi.decorators;

import java.util.Map;
import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

/**
 * @author Antoine Sabot-Durand
 */

@Decorator
@Priority(Interceptor.Priority.APPLICATION)
public abstract class MapDecorator implements Map{
    
    @Inject
    @Delegate
    Map delegate;


    @Override
    public Object put(Object key, Object value) {
        System.out.println("------- Putting Something in the Map -----------");
        if ("key".equals(key)) {
            System.out.println("==== Not adding key key ======");
            return null;
        }
        
        return delegate.put(key,value);
    }
}
