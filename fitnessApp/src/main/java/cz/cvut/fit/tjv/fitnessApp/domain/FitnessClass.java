package cz.cvut.fit.tjv.fitnessApp.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class FitnessClass extends IdentifiableImpl<Integer> {
    private int capacity;
    private LocalDate date;
    private LocalTime time;
    private Instructor instructor;
    private Room room;
    private ClassType classType;
    private final Set<Trainee> trainees = new HashSet<>();

    public FitnessClass(Integer id, int capacity, LocalDate date, LocalTime time, Instructor instructor, Room room, ClassType classType) {
        this.id = id;
        this.capacity = capacity;
        this.date = date;
        this.time = time;
        this.instructor = instructor;
        this.room = room;
        this.classType = classType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public Set<Trainee> getTrainees() {
        return new HashSet<>(trainees);
    }

    public void addTrainee(Trainee trainee) {
        this.trainees.add(trainee);
    }

    public void removeTrainee(Trainee trainee) {
        this.trainees.remove(trainee);
    }
}