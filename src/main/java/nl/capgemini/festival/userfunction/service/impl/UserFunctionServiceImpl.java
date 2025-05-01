package nl.capgemini.festival.userfunction.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import nl.capgemini.festival.userfunction.entity.UserFunction;
import nl.capgemini.festival.userfunction.repository.UserFunctionRepository;
import nl.capgemini.festival.userfunction.service.UserFunctionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFunctionServiceImpl implements UserFunctionService {

    Logger logger = LogManager.getLogger(UserFunctionServiceImpl.class);

    @Autowired
    UserFunctionRepository repository;

    @Override
    @Transactional
    public void save(UserFunction function) {
        if (function.getId() != null) {
            logger.info("-- Found role-ID:" + function.getId() + ", validating against repository.");
        }
        List<UserFunction> functions = repository.findByName(function.getName());
        if (!functions.isEmpty()) {
            for (UserFunction foundFunction : functions) {
                if (foundFunction.getName().equals(function.getName())){
                    return;
                }
            }
        }
        repository.save(function);
        logger.info("Finished process: {saving-role:[role: " + function.getName() + "]}");
        System.out.println("Saving UserFunction: " + function);
    }

    @Override
    public UserFunction findByName(String name) {
        List<UserFunction> functions = repository.findByName(name);
        if (functions.isEmpty()){
            logger.warn("No user-function found with name: " + name);
            return null;
        }
        if (functions.size() > 1){
            logger.warn("Found more than one user-function with name: " + name);
        }
        return functions.get(0); // We make the assumption that there is only one of each function
//        todo: assumption is the mother of all fuckups
    }

    @Override
    public UserFunction merge(UserFunction function, EntityManager manager) {
        return manager.merge(function);
    }
}
