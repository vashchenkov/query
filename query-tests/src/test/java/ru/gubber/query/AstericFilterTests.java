package ru.gubber.query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import ru.gubber.query.filter.AsterickFilter;
import ru.gubber.query.filter.CompositeFilter;
import ru.gubber.query.filter.Filter;
import testy.gubber.query.model.Dog;
import testy.gubber.query.model.TestBean;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class AstericFilterTests {
    private static Session session;

    @BeforeClass
    public static void initTestEnvironment(){
        String filename = PagedListTest.class.getClassLoader().getResource("").getFile();
        System.getProperties().put("h2.baseDir", filename);
        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        session = sessionFactory.openSession();

        List list = session.createQuery("from Dog").list();
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    public void testFindFirstLevelSubFilter() {
        PagedList pagedList = new PagedList(Dog.class);

        AsterickFilter filter = new AsterickFilter("id", "1");

        pagedList.setFilter(filter);

        Collection dogs = pagedList.getItems(session);
    }

}
