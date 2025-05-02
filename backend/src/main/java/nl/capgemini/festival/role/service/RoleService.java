package nl.capgemini.festival.role.service;

import jakarta.persistence.EntityManager;
import nl.capgemini.festival.role.entity.Role;

public interface RoleService {
    void save(Role role);

    Role getAccessRoleHighest();

    Role getAccessRoleLowest();

    Role merge(Role role, EntityManager entityManager);
}
