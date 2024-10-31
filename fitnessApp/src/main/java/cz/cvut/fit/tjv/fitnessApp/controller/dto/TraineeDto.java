package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class TraineeDto {
    private Integer id;
    private String email;
    private String name;
    private String surname;
    private Set<FitnessClassDto> classes;
    public TraineeDto(Integer id, String email, String name, String surname, Set<FitnessClassDto> classes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.classes = classes;
    }
}