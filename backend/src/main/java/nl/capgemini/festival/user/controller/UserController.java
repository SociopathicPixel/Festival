package nl.capgemini.festival.user.controller;

import nl.capgemini.festival.user.entity.User;
import nl.capgemini.festival.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    public void save(User user) {
        service.save(user);
    }
}
