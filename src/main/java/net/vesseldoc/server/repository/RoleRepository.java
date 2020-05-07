package net.vesseldoc.server.repository;

import net.vesseldoc.server.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to get user role related information from database.
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

    Role getRoleById(long id);

    Role getRoleByName(String name);
}
