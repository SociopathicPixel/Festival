package nl.capgemini.festival.userfunction.repository;

import nl.capgemini.festival.userfunction.entity.UserFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFunctionRepository extends JpaRepository<UserFunction, Long> {

    List<UserFunction> findByName(String name);
}
