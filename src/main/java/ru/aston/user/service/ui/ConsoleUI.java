package ru.aston.user.service.ui;

import ru.aston.user.service.entity.User;
import ru.aston.user.service.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleUI() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getUserChoice();
            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> findById();
                    case 3 -> listAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> {
                        running = false;
                        System.out.println("Goodbye");
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
                System.out.println("=".repeat(50));
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           USER SERVICE MANAGEMENT");
        System.out.println("=".repeat(50));
        System.out.println("1. Create New User");
        System.out.println("2. Find User by ID");
        System.out.println("3. List All Users");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.println("0. Exit");
        System.out.println("=".repeat(50));
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void createUser() {
        System.out.println("\n--- Create New User ---");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your age: ");
        int age = scanner.nextInt();

        User user = userService.createUser(name, email, age);
        System.out.println("User created successfully");
        displayUser(user);
    }

    private void findById() {
        System.out.println("\n--- Find User by ID ---");
        System.out.print("Enter user ID: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            displayUser(user.get());
        } else {
            System.out.println("User not found with ID: " + id);
        }
    }

    private void listAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found");
        } else {
            users.forEach(this::displayUser);
        }
    }

    private void updateUser() {
        System.out.println("\n--- Update User ---");
        System.out.print("Enter user ID to update: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            System.out.println("User not found with ID: " + id);
            return;
        }

        User existingUser = userOpt.get();
        System.out.println("Current user details:");
        displayUser(existingUser);

        System.out.println("\nEnter new values (press Enter to keep current value):");
        System.out.print("Name [" + existingUser.getName() + "]: ");
        String name = scanner.nextLine();
        System.out.print("Email [" + existingUser.getEmail() + "]: ");
        String email = scanner.nextLine();
        System.out.print("Age [" + existingUser.getAge() + "]: ");
        int age = scanner.nextInt();

        User updatedUser = userService.updateUser(id,
                name.isEmpty() ? existingUser.getName() : name,
                email.isEmpty() ? existingUser.getEmail() : email,
                age <= 0 ? existingUser.getAge() : age);

        System.out.println("User updated successfully:");
        displayUser(updatedUser);
    }

    private void deleteUser() {
        System.out.println("\n--- Delete User ---");
        System.out.print("Enter user ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            System.out.println("User not found with ID: " + id);
            return;
        }

        System.out.print("Are you sure you want to delete user with ID " + id + "? (y/n): ");
        String confirm = scanner.nextLine();

        if ("y".equalsIgnoreCase(confirm)) {
            userService.deleteUser(id);
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void displayUser(User user) {
        System.out.println("\nUser ID: " + user.getId());
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Age: " + user.getAge());
        System.out.println("Created: " + user.getCreatedAt());
    }
}
