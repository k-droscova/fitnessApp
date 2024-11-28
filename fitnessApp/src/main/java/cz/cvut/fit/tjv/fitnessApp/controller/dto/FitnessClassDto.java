package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class FitnessClassDto {
    private Integer id;
    private int capacity;
    private LocalDate date;
    private LocalTime time;
    private Integer instructorId;
    private Integer roomId;
    private Integer classTypeId;
    private Set<Integer> traineeIds = new HashSet<>();
}