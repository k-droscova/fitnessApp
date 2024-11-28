package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class TraineeDto {
    private Integer id;
    private String email;
    private String name;
    private String surname;
    private Set<Integer> fitnessClassIds = new HashSet<>();
}