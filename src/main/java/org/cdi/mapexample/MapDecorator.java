package org.cdi.mapexample;

import java.util.Map;
import javax.annotation.Priority;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
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

    @Inject
    Event<String> msgEvents;


    @Override
    public Object put(Object key, Object value) {

        if ("key".equals(key)) {
            System.out.println("==== Not adding key key ======");
            return null;
        }

        msgEvents.select(new AnnotationLiteral<AddedToMap>() {}).fire(key.toString() + " / " + value.toString());
        return delegate.put(key,value);
    }
}
