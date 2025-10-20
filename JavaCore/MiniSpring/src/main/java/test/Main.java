package test;

import minispring.MiniApplicationContext;

public class Main {
    public static void main(String[] args) {
        try {
            MiniApplicationContext context = new MiniApplicationContext("test");

            UserService userService1 = context.getBean(UserService.class);
            UserService userService2 = context.getBean(UserService.class);
            System.out.println("Same UserService instance: " + (userService1 == userService2));
            System.out.println("UserService result: " + userService1.processUser());
            System.out.println();

            PrototypeService proto1 = context.getBean(PrototypeService.class);
            PrototypeService proto2 = context.getBean(PrototypeService.class);
            System.out.println("Same PrototypeService instances: " + (proto1 == proto2));
            proto1.display();
            proto2.display();
            System.out.println();

            UserController controller = context.getBean(UserController.class);
            controller.display();
            System.out.println();

            UserRepository repo1 = context.getBean(UserRepository.class);
            UserRepository repo2 = context.getBean(UserRepository.class);
            System.out.println("Repo call from service: " + userService1.processUser());
            System.out.println("Repo call directly: " + repo1.getData());
            System.out.println("Same repository instances: " + (repo1 == repo2));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}