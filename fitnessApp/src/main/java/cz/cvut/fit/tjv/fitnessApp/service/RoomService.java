package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Room;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RoomService extends CrudService<Room, Long> {
    List<Room> findAvailableRooms(Optional<Long> classTypeId, LocalDate date, LocalTime time);
}
