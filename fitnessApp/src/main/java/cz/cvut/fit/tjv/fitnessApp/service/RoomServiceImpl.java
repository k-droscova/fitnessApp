package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl extends CrudServiceImpl<Room, Long> implements RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    protected CrudRepository<Room, Long> getRepository() {
        return roomRepository;
    }

    @Override
    public List<Room> findAvailableRooms(Optional<Long> classTypeId, LocalDate date, LocalTime time) {
        return roomRepository.findAvailableRoomsByOptionalClassType(
                classTypeId.orElse(null), // If empty, pass null to fetch all rooms
                date,
                time
        );
    }
}