package cz.cvut.fit.tjv.fitnessApp.controller.dto.fitnessClass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class FitnessClassDto {
    private Long id;
    private int capacity;
    private LocalDate date;
    private LocalTime time;
    private Long instructorId;
    private Long roomId;
    private Long classTypeId;
    private List<Long> traineeIds = new ArrayList<>();
}