package test;

import minispring.annotations.Autowired;
import minispring.annotations.Component;

@Component
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PrototypeService prototypeService1;

    @Autowired
    private PrototypeService prototypeService2;

    public void display() {
        System.out.println("UserService: " + userService.processUser());
        System.out.println("PrototypeService1: " + prototypeService1);
        System.out.println("PrototypeService2: " + prototypeService2);
        System.out.println("Same prototype instances: " + (prototypeService1 == prototypeService2));
    }
}