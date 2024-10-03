package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.respository.CrudRepository;
import cz.cvut.fit.tjv.fitnessApp.respository.RoomRepository;

public class RoomServiceImpl extends CrudServiceImpl<Room, Integer> implements RoomService {
    private RoomRepository roomRepository;
    @Override
    protected CrudRepository<Room, Integer> getRepository() {
        return roomRepository;
    }
}