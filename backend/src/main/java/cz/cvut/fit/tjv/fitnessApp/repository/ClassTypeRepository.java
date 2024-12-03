package cz.cvut.fit.tjv.fitnessApp.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassTypeRepository extends CrudRepository<ClassType, Long> {
    @Query("SELECT c FROM ClassType c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ClassType> findByNameContainingIgnoreCase(@Param("name") String name);
}
