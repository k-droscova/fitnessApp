package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import java.time.LocalDate;
import java.util.Set;

public class InstructorDto {

    private Integer id;
    private String name;
    private String surname;
    private LocalDate birthDate;

    private Set<ClassTypeDto> specializations;
    private Set<FitnessClassDto> classes;

    public InstructorDto() {}

    public InstructorDto(Integer id, String name, String surname, LocalDate birthDate, Set<ClassTypeDto> specializations, Set<FitnessClassDto> classes) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.specializations = specializations;
        this.classes = classes;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Set<ClassTypeDto> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(Set<ClassTypeDto> specializations) {
        this.specializations = specializations;
    }

    public Set<FitnessClassDto> getClasses() {
        return classes;
    }

    public void setClasses(Set<FitnessClassDto> classes) {
        this.classes = classes;
    }
}