package test;

import minispring.InitializingBean;
import minispring.annotations.Autowired;
import minispring.annotations.Component;

@Component
public class UserService implements InitializingBean {
    @Autowired
    private UserRepository userRepository;

    public String processUser() {
        return "Processed: " + userRepository.getData();
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("UserService init, repository injection = " + (userRepository != null));
    }
}
