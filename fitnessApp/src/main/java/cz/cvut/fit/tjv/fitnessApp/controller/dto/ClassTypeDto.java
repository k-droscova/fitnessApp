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

public class ClassTypeDto {
    private Integer id;
    private String name;
    private Set<Integer> instructorIds = new HashSet<>();
    private Set<Integer> roomIds = new HashSet<>();
    private Set<Integer> fitnessClassIds = new HashSet<>();
}
