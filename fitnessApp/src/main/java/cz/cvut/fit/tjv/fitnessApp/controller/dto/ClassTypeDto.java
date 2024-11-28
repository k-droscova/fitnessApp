package cz.cvut.fit.tjv.fitnessApp.controller.dto;

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

public class ClassTypeDto {
    private Long id;
    private String name;
    private List<Long> instructorIds = new ArrayList<>();
    private List<Long> roomIds = new ArrayList<>();
    private List<Long> fitnessClassIds = new ArrayList<>();
}
