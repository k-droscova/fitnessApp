package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import java.util.Set;

public class ClassTypeDto {
    private Integer id;
    private String name;
    private Set<InstructorDto> instructors;
    private Set<RoomDto> rooms;
    private Set<FitnessClassDto> fitnessClasses;

    public ClassTypeDto() {}

    public ClassTypeDto(Integer id, String name, Set<InstructorDto> instructors, Set<RoomDto> rooms, Set<FitnessClassDto> fitnessClasses) {
        this.id = id;
        this.name = name;
        this.instructors = instructors;
        this.rooms = rooms;
        this.fitnessClasses = fitnessClasses;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<InstructorDto> getInstructors() {
        return instructors;
    }

    public void setInstructors(Set<InstructorDto> instructors) {
        this.instructors = instructors;
    }

    public Set<RoomDto> getRooms() {
        return rooms;
    }

    public void setRooms(Set<RoomDto> rooms) {
        this.rooms = rooms;
    }

    public Set<FitnessClassDto> getFitnessClasses() {
        return fitnessClasses;
    }

    public void setFitnessClasses(Set<FitnessClassDto> fitnessClasses) {
        this.fitnessClasses = fitnessClasses;
    }
}
