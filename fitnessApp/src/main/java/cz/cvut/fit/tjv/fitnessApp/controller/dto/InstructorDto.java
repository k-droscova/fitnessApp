package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class InstructorDto {

    private Integer id;
    private String name;
    private String surname;
    private LocalDate birthDate;

    private Set<ClassTypeDto> specializations;
    private Set<FitnessClassDto> classes;

    public InstructorDto(Integer id, String name, String surname, LocalDate birthDate, Set<ClassTypeDto> specializations, Set<FitnessClassDto> classes) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.specializations = specializations;
        this.classes = classes;
    }
}