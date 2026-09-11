package ru.aston.user.service;

import ru.aston.user.service.config.HibernateUtil;
import ru.aston.user.service.ui.ConsoleUI;

public class Main {
    public static void main(String[] args){
        try {
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
