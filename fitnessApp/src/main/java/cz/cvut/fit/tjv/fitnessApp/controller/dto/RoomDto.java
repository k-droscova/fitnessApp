package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import java.util.Set;

public class RoomDto {

    private Integer id;
    private int maxCapacity;

    private Set<FitnessClassDto> classes;
    private Set<ClassTypeDto> classTypes;

    public RoomDto() {}

    public RoomDto(Integer id, int maxCapacity, Set<FitnessClassDto> classes, Set<ClassTypeDto> classTypes) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.classes = classes;
        this.classTypes = classTypes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Set<FitnessClassDto> getClasses() {
        return classes;
    }

    public void setClasses(Set<FitnessClassDto> classes) {
        this.classes = classes;
    }

    public Set<ClassTypeDto> getClassTypes() {
        return classTypes;
    }

    public void setClassTypes(Set<ClassTypeDto> classTypes) {
        this.classTypes = classTypes;
    }
}