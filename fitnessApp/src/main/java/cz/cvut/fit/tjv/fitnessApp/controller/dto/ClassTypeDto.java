package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class ClassTypeDto {
    private Integer id;
    private String name;
    private Set<InstructorDto> instructors;
    private Set<RoomDto> rooms;
    private Set<FitnessClassDto> fitnessClasses;

    public ClassTypeDto(Integer id, String name, Set<InstructorDto> instructors, Set<RoomDto> rooms, Set<FitnessClassDto> fitnessClasses) {
        this.id = id;
        this.name = name;
        this.instructors = instructors;
        this.rooms = rooms;
        this.fitnessClasses = fitnessClasses;
    }
}
