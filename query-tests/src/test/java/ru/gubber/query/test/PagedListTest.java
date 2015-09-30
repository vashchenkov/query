package ru.gubber.query.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Created by gubber on 29.09.2015.
 */
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
	public void firstTest(){
	}
}
