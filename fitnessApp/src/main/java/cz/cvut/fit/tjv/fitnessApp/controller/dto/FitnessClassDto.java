package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class FitnessClassDto {
    private Integer id;
    private int capacity;
    private LocalDate date;
    private LocalTime time;
    private InstructorDto instructor;
    private RoomDto room;
    private ClassTypeDto classType;
    private Set<TraineeDto> trainees;

    public FitnessClassDto(Integer id, int capacity, LocalDate date, LocalTime time,
                           InstructorDto instructor, RoomDto room, ClassTypeDto classType,
                           Set<TraineeDto> trainees) {
        this.id = id;
        this.capacity = capacity;
        this.date = date;
        this.time = time;
        this.instructor = instructor;
        this.room = room;
        this.classType = classType;
        this.trainees = trainees;
    }
}