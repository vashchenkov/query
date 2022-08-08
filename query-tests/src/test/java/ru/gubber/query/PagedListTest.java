package ru.gubber.query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import ru.gubber.query.filter.EmptyFieldFilter;
import ru.gubber.query.filter.Filter;
import ru.gubber.query.filter.NegationFilter;
import testy.gubber.query.model.Dog;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by gubber on 29.09.2015.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class PagedListTest{
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
	public void emptyFieldFilterTest(){
		Filter emptyFatherFilter = new EmptyFieldFilter("father");
		PagedList list = new PagedList(Dog.class, emptyFatherFilter);
		Collection dogs = list.getItems(session);
		assertTrue(dogs.size() == 2);

		Filter nonEmptyFatherFilter = new NegationFilter(emptyFatherFilter);
		list = new PagedList(Dog.class, nonEmptyFatherFilter);
		dogs = list.getItems(session);
		assertTrue(dogs.size() == 1);
	}
}
