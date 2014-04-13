package org.cdi.jpa;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author Antoine Sabot-Durand
 */
@RunWith(Arquillian.class)
public class EntityIsBeanTest {

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {

        WebArchive ret = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addClasses(Movie.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("META-INF/create.sql")
                .addAsResource("META-INF/load.sql")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return ret;
    }

    @Inject
        Instance<Movie> myMovies;

    @Test
    public void movieShouldExist() {
        Assert.assertFalse(myMovies.isUnsatisfied());
    }

}
