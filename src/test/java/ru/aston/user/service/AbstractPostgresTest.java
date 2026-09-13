package ru.aston.user.service;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

import org.hibernate.cfg.Configuration;

public abstract class AbstractPostgresTest {

    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:14")
                    .withDatabaseName("user_service_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    public static SessionFactory sessionFactory;

    @BeforeAll
    static void startContainerAndBuildSessionFactory() {
        POSTGRES.start();

        sessionFactory = new Configuration()
                .configure("hibernate-test.cfg.xml")
                .setProperty("hibernate.connection.url", POSTGRES.getJdbcUrl())
                .setProperty("hibernate.connection.username", POSTGRES.getUsername())
                .setProperty("hibernate.connection.password", POSTGRES.getPassword())
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) sessionFactory.close();
    }
}