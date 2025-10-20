package test;

import minispring.annotations.Component;

@Component
public class UserRepository {
    private int callCount = 0;

    public String getData() {
        callCount++;
        return "Data from repository(call count = " + callCount + ")";
    }
}
