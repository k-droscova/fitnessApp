package cz.cvut.fit.tjv.fitnessApp.controller.dto.trainee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class TraineeDto {
    private Long id;
    private String email;
    private String name;
    private String surname;
    private List<Long> fitnessClassIds = new ArrayList<>();
}