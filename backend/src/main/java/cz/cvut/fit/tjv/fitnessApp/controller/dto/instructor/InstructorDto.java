package cz.cvut.fit.tjv.fitnessApp.controller.dto.instructor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class InstructorDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private List<Long> classTypeIds = new ArrayList<>();
    private List<Long> fitnessClassIds = new ArrayList<>();
}