package ru.aston.user.service;

import ru.aston.user.service.entity.User;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args){

        User user = new User();
        user.setAge(12);
        user.setName("света");
        user.setEmail("svetochka@dev.ru");
        user.setCreatedAt(LocalDateTime.now());

        System.out.println(user);
    }
}
