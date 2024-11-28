package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class InstructorDto {
    private Integer id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private Set<Integer> classTypeIds = new HashSet<>();
    private Set<Integer> fitnessClassIds = new HashSet<>();
}