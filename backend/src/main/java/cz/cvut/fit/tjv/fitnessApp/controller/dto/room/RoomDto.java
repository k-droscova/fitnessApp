package cz.cvut.fit.tjv.fitnessApp.controller.dto.room;

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

public class RoomDto {
    private Long id;
    private int maxCapacity;
    private List<Long> fitnessClassIds = new ArrayList<>();
    private List<Long> classTypeIds = new ArrayList<>();
}