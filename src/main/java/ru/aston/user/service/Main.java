package ru.aston.user.service;

import ru.aston.user.service.config.HibernateUtil;
import ru.aston.user.service.dao.UserDao;
import ru.aston.user.service.dao.UserDaoImpl;
import ru.aston.user.service.entity.User;
import ru.aston.user.service.service.UserService;
import ru.aston.user.service.ui.ConsoleUI;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        if (HibernateUtil.getSessionFactory() != null) {
            System.out.println("Config Hibernate");
        }

        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.start();
    }
}
