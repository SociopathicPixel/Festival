package nl.capgemini.festival.userfunction.controller;

import jakarta.persistence.EntityManager;
import nl.capgemini.festival.userfunction.entity.UserFunction;
import nl.capgemini.festival.userfunction.service.UserFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserFunctionController {

    @Autowired
    UserFunctionService service;

    public void save(UserFunction function) {service.save(function);}

    public UserFunction findByName(String name) { return service.findByName(name);}

    public UserFunction merge(UserFunction function, EntityManager entityManager) {return service.merge(function, entityManager);}
}
