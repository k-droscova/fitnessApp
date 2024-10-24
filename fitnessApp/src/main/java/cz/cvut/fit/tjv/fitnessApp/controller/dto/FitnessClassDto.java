package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class FitnessClassDto {
    private Integer id;
    private int capacity;
    private LocalDate date;
    private LocalTime time;
    private InstructorDto instructor;
    private RoomDto room;
    private ClassTypeDto classType;
    private Set<TraineeDto> trainees;

    public FitnessClassDto() {}

    public FitnessClassDto(Integer id, int capacity, LocalDate date, LocalTime time,
                           InstructorDto instructor, RoomDto room, ClassTypeDto classType,
                           Set<TraineeDto> trainees) {
        this.id = id;
        this.capacity = capacity;
        this.date = date;
        this.time = time;
        this.instructor = instructor;
        this.room = room;
        this.classType = classType;
        this.trainees = trainees;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public InstructorDto getInstructor() {
        return instructor;
    }

    public void setInstructor(InstructorDto instructor) {
        this.instructor = instructor;
    }

    public RoomDto getRoom() {
        return room;
    }

    public void setRoom(RoomDto room) {
        this.room = room;
    }

    public ClassTypeDto getClassType() {
        return classType;
    }

    public void setClassType(ClassTypeDto classType) {
        this.classType = classType;
    }

    public Set<TraineeDto> getTrainees() {
        return trainees;
    }

    public void setTrainees(Set<TraineeDto> trainees) {
        this.trainees = trainees;
    }
}