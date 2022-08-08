package ru.gubber.query;


import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import testy.gubber.query.model.Dog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaseTest {
    private static Session session;

    public BaseTest() throws Throwable {
        Properties properties = new Properties();
// load properties from classpath

        java.sql.Connection connection = openConnection(); //your openConnection logic here
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        try (Liquibase liquibase = new liquibase.Liquibase("./changelog.xml", new ClassLoaderResourceAccessor(), database)){
            properties.forEach((key, value) -> liquibase.setChangeLogParameter(Objects.toString(key), value));
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }

    Connection openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        String jdbcUrl = "jdbc:h2:file:/Users/ruavcak/projects/personal/query/query-tests/target/query-testing";
        Connection connection = DriverManager.getConnection(jdbcUrl, "", "");
        return connection;
    }

    @Test
    public void x(){

        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        session = sessionFactory.openSession();
        Dog o = new Dog();
        session.save(o);

        List list = session.createQuery("from Dog").list();
        assertNotNull(list);
        assertTrue(list.size() > 0);

    }
}
