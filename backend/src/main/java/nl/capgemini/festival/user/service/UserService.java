package nl.capgemini.festival.user.service;

import nl.capgemini.festival.user.entity.User;

public interface UserService {


    User save(User user);
    boolean validatePasswordRequirements(String password);
}
