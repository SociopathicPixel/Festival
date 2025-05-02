package nl.capgemini.festival.userfunction.service;

import jakarta.persistence.EntityManager;
import nl.capgemini.festival.userfunction.entity.UserFunction;

public interface UserFunctionService {
    void save(UserFunction function);

    UserFunction findByName(String name);

    UserFunction merge(UserFunction function, EntityManager entityManager);
}
