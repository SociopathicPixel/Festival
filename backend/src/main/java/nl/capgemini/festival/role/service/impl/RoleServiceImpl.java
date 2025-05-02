package nl.capgemini.festival.role.service.impl;

import jakarta.persistence.EntityManager;
import nl.capgemini.festival.role.entity.Role;
import nl.capgemini.festival.role.repository.RoleRepository;
import nl.capgemini.festival.role.service.RoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    Logger logger = LogManager.getLogger(RoleService.class);

    @Autowired
    RoleRepository repository;

    @Override
    @Transactional
    public void save(Role role) {
        logger.info("Starting process: {saving role:[role: " + role.getName() + "]}");
        if (role.getId() != null) {
            // TODO: Create update role method to be placed here...
            logger.info("-- Found role-ID: " + role.getId() + ", validating against repository.");
        }
        List<Role> roles = repository.findByName(role.getName());
        if (roles.isEmpty()) {
            for (Role foundRole : roles){
                logger.info("-- Found " + roles.size() + " role(s) with similar name, validating against repository.");
                if (foundRole.getName().equals(role.getName())){
                    logger.info("-- Found duplicate. Aborting process: {saving role}");
                    return;
                }
            }
        }
        repository.save(role);
        logger.info("Finished process: {saving role:[role: " + role.getName() + "]}");
    }

    @Override
    public Role getAccessRoleHighest() {
        List<Role> allRoles = repository.findAll();
        return allRoles.stream()
                .min(Comparator.comparingInt(Role::getAccessLvl))
                .orElse(null);
    }

    @Override
    public Role getAccessRoleLowest() {
        List<Role> allRoles = repository.findAll();
        return allRoles.stream()
                .max(Comparator.comparingInt(Role::getAccessLvl))
                .orElse(null);
    }

    @Override
    @Transactional
    public Role merge(Role role, EntityManager manager) {
        return manager.merge(role);
    }
}
