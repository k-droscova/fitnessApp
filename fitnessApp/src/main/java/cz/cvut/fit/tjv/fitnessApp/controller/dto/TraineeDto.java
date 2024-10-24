package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import java.util.Set;

public class TraineeDto {
    private Integer id;
    private String email;
    private String name;
    private String surname;
    private Set<FitnessClassDto> classes;
    public TraineeDto() {}

    public TraineeDto(Integer id, String email, String name, String surname, Set<FitnessClassDto> classes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.classes = classes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Set<FitnessClassDto> getClasses() {
        return classes;
    }

    public void setClasses(Set<FitnessClassDto> classes) {
        this.classes = classes;
    }
}
