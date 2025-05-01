package nl.capgemini.festival.role.controller;

import jakarta.persistence.EntityManager;
import nl.capgemini.festival.role.entity.Role;
import nl.capgemini.festival.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RoleController {
    @Autowired
    private RoleService service;

    public void save(Role role ) { service.save(role);}

    public Role getHighestAccessRole() { return service.getAccessRoleHighest();}

    public Role getLowestAccessRole() { return service.getAccessRoleLowest();}

    public Role merge(Role role, EntityManager entityManager) { return service.merge(role, entityManager);}
}
