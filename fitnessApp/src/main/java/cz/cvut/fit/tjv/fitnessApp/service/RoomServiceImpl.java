package cz.cvut.fit.tjv.fitnessApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import org.springframework.data.repository.CrudRepository;

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
}