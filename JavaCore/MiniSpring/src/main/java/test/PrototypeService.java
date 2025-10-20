package test;

import minispring.annotations.Autowired;
import minispring.annotations.Component;
import minispring.annotations.Scope;

@Component
@Scope("prototype")
public class PrototypeService {
    private static int instanceCount = 0;
    private final int instanceNumber;

    @Autowired
    private UserRepository userRepository;

    public PrototypeService() {
        instanceCount++;
        instanceNumber = instanceCount;
        System.out.println("Prototype instance №" + instanceNumber);
    }

    public void display() {
        System.out.println("Prototype instance №" + instanceNumber + ", Repository = " + (userRepository != null));
    }
}