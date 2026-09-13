package ru.aston.user.service.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory SESSION_FACTORY =
            new Configuration()
                    .configure()
                    .buildSessionFactory();

    public static SessionFactory getSessionFactory() {
        if (SESSION_FACTORY == null) {
            SESSION_FACTORY = new Configuration()
                    .configure()
                    .buildSessionFactory();
        }
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}