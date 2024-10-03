package cz.cvut.fit.tjv.fitnessApp.domain;

import java.util.HashSet;
import java.util.Set;

public class ClassType extends IdentifiableImpl<Integer> {
    private String name;
    private final Set<Instructor> instructors = new HashSet<>();
    private final Set<Room> rooms = new HashSet<>();

    public ClassType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Instructor> getInstructors() {
        return new HashSet<>(instructors);
    }

    public void addInstructor(Instructor instructor) {
        this.instructors.add(instructor);
    }

    public void removeInstructor(Instructor instructor) {
        this.instructors.remove(instructor);
    }

    public Set<Room> getRooms() {
        return new HashSet<>(rooms);
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room);
    }
}