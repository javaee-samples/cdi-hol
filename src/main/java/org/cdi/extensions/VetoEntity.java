package org.cdi.extensions;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.persistence.Entity;

/**
 * @author Antoine Sabot-Durand
 */
public class VetoEntity implements Extension {

    /**
     * Version 1.0
     */
    public void vetoEntity(@Observes @WithAnnotations({ Entity.class }) ProcessAnnotatedType<?> pat) {
        System.out.println("**********  Found class with @Entity annotation : " + pat.getAnnotatedType().getJavaClass());
        pat.veto();

        AnnotatedType at = pat.getAnnotatedType();
        if (at.isAnnotationPresent(Entity.class))
            pat.veto();
    }

}
