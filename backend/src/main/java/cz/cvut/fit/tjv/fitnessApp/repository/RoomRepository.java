package cz.cvut.fit.tjv.fitnessApp.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {

    @Query("""
        SELECT r
        FROM Room r
        LEFT JOIN r.classTypes ct
        WHERE (:classTypeId IS NULL OR ct.id = :classTypeId)
          AND r.id NOT IN (
              SELECT fc.room.id
              FROM FitnessClass fc
              WHERE fc.date = :date
                AND fc.time = :time
          )
    """)
    List<Room> findAvailableRoomsByOptionalClassType(
            @Param("classTypeId") Long classTypeId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );
}
