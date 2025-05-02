package nl.capgemini.festival.user.service.impl;

import jakarta.persistence.EntityManager;
import nl.capgemini.festival.role.controller.RoleController;
import nl.capgemini.festival.role.entity.Role;
import nl.capgemini.festival.user.entity.User;
import nl.capgemini.festival.user.repository.UserRepository;
import nl.capgemini.festival.user.service.UserService;
import nl.capgemini.festival.userfunction.controller.UserFunctionController;
import nl.capgemini.festival.userfunction.entity.UserFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LogManager.getLogger(UserService.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Autowired
    UserRepository repository;
    @Autowired
    RoleController roleController;
    @Autowired
    UserFunctionController functionController;
    @Autowired
    EntityManager entityManager;

    @Override
    @Transactional
    public User save(User user) {
        if (user == null) throw new DataIntegrityViolationException("User cannot be null.");
        logger.info("Start process: {saving user:[user: " + user.getUsername() + "]}");
        if (user.getId() != null){
            logger.info("-- Found user-ID: " + user.getId() + ", validating against repository.");
            Optional<User> optionalUser = repository.findById(user.getId());
            if (optionalUser.isPresent()) user = updateUser(optionalUser.get(), user);
        }
        List<User> foundUsers = findAllByName(user.getUsername());
        if (user.getId() == null && !foundUsers.isEmpty()){
            logger.info("Found more than one user with the same username: " + user.getUsername() + ", validating against repository.");
            for (User foundUser : foundUsers){
                if (passwordEncoder.matches(user.getPassword(), foundUser.getPassword())){
                    logger.info("-- Found duplicate, update user: " + user.getUsername());
                    user = updateUser(foundUser, user);
                    break;
                }
            }
        }
        manageUserRoles(user);
        manageUserFunctions(user);
        validatePassword(user);
        if (user.getUid() == null) generateUniqueUid(user);
        if (user.getId() == null) { user = repository.save(user); }
        generateUniqueUid(user);
        User savedUser = repository.save(user);
        logger.info("Finish process: {saving-user: [" + savedUser.getUid() + "]}");
        return savedUser;
    }

    private String generateUniqueUid(User user) {
        logger.info("-- Generating UID");
        if (user.getId() == null) {
            logger.info("-- User has no ID, generating temporary UID.");
            user.setUid("temp-" + user.getUsername().replace(" ", "_"));
        } else {
            user.setUid(user.getId() + "-" + user.getUsername().replace(" ", "_"));
        }
       return user.getUid();
    }

    private void validatePassword(User user) {
        logger.info("Start process: {validating password:[user: " + user.getUsername() + "]}");
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            logger.warn("-- Encryption check failed! Password cannot be null or empty.");
            throw new DataIntegrityViolationException("Password cannot be null or empty.");
        }
        if (!user.isEncrypted()) {
            if (!validatePasswordRequirements(user.getPassword())){
                logger.warn("-- Encryption check failed! Password does not meet requirements.");
                logger.fatal("Aborting process: {saving user:[user: " + user.getUsername() + "]}");
                throw new DataIntegrityViolationException("Password does not meet requirements.");
            }
            if (user.getPassword().length() == 60) {
                logger.warn("-- Encryption check failed! Password is already encrypted.");
            } else {
                logger.info("-- Encrypting password.");
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                logger.info("-- Password encrypted.");
            }
            user.setEncrypted(true);
            logger.info("-- Updating encryption check to true.");
        }
        if (user.getPassword().length() != 60 && user.isEncrypted()) {
            logger.warn("-- Encryption is inconsistent with HASH length.");
            user.setEncrypted(false);
            logger.info("-- Updating encryption check to false.");
            validatePassword(user);
        }
        logger.info("Finish process: {validating password:[user: " + user.getUsername() + "]}");
    }

    @Override
    public boolean validatePasswordRequirements(String password) {
        logger.info("-- Validating password requirements.");
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w])[A-Za-z\\d\\W]{8,32}$"
                ;
        return Pattern.matches(pattern, password);
    }

    private void manageUserRoles(User user) {
        Set<Role> userRoles = user.getRoles();
        if (userRoles.stream().noneMatch(role -> role.getName().equals(roleController.getLowestAccessRole().getName()))) {
            userRoles.add(roleController.getLowestAccessRole());
        }
        user.setRoles(new HashSet<>());
        for (Role role : userRoles) {
            if (role != null) addRoleToUser(user, role);
        }
    }

    @Transactional
    private void addRoleToUser(User user, Role role) {
        logger.info("Start process: {add-role2user:[username: "+ user.getUsername()+", role: "+role.getName()+"]}");
        Role managedRole = roleController.merge(role, entityManager);
        user.addRole(managedRole);
        logger.info("Finish process: {add-role2user}");
    }

    @Transactional
    private void addFunctionToUser(User user, UserFunction function){
        logger.info("Start process: {add-function2user:[username: "+ user.getUsername()+", function: "+function.getName()+"]}");
        UserFunction managedFunction = functionController.merge(function, entityManager);
        user.addFunction(managedFunction);
        logger.info("Finish process: {add-function2user}");
    }

    private void manageUserFunctions(User user){
        Set<UserFunction> functions = user.getFunctions();
        user.setFunctions(new HashSet<>());
        for (UserFunction function : functions) {
            if (function != null) addFunctionToUser(user, function);
        }
    }

    private List<User> findAllByName(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return repository.findByUsername(username)
                .map(List::of)
                .orElse(List.of());
    }

    public User updateUser(User oldUser, User newUser){
        return updateUser(oldUser, newUser, false);
    }
    public User updateUser(User original, User newUser, boolean deepUpdate){
        logger.info("Start process: {updating user:[user: " + original.getUsername() + "]}");
        User updatedUser = original.deepCopy();
        if (newUser.getUsername() != null) updatedUser.setUsername(newUser.getUsername());
        if (newUser.getPassword() != null) newUser.setPassword(newUser.getPassword());
        if (newUser.isEncrypted() && newUser.getPassword() != null) newUser.setEncrypted(true);

        if (newUser.getEmail() != null) newUser.setEmail(newUser.getEmail());
        if (newUser.getDob() != null) newUser.setDob(newUser.getDob());
        if (deepUpdate){
            logger.info("-- Initializing deep update.");
            if (newUser.getRoles() != null) updatedUser.setRoles(newUser.getRoles());
            if (newUser.getFunctions() != null) updatedUser.setFunctions(newUser.getFunctions());
        }
        validatePassword(updatedUser);
        if (updatedUser.getUid() == null) generateUniqueUid(updatedUser);
        logger.info("Finish process: {updating user:[user: " + updatedUser.getUsername() + "]}");
        return updatedUser;
    }
}
