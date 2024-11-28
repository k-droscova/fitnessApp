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

public class RoomDto {
    private Integer id;
    private int maxCapacity;
    private Set<Integer> fitnessClassIds = new HashSet<>();
    private Set<Integer> classTypeIds = new HashSet<>();
}