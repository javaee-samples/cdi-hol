package org.cdi;

import junit.framework.Assert;
import org.cdi.extensions.HashMapAsBeanExtension;
import org.cdi.mapexample.AddedToMap;
import org.cdi.mapexample.MapDecorator;
import org.cdi.mapexample.MyMapObserver;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.util.Map;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

/**
 * @author Antoine Sabot-Durand
 */
@RunWith(Arquillian.class)
public class HashMapAsBeanTest {

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {

        WebArchive ret = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addClasses(AddedToMap.class,
                        MyMapObserver.class,
                        HashMapAsBeanExtension.class,
                        MapDecorator.class)
                .addAsServiceProvider(Extension.class, HashMapAsBeanExtension.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return ret;
    }

    @Inject
    private
    Map<String,String> myMap;

    @Test
    public void mapShouldExist() {
        Assert.assertNotNull(myMap);
    }
    
    @Test
    public void mapShouldNotAddKeyAsKy() {
        myMap.put("key","value");
        myMap.put("anotherKey","anotherValue");
        
        Assert.assertFalse(myMap.containsKey("key"));
        Assert.assertTrue(myMap.containsKey("anotherKey"));
        
    }

}
