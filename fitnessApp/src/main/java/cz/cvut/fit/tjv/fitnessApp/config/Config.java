package cz.cvut.fit.tjv.fitnessApp.config;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.*;
import cz.cvut.fit.tjv.fitnessApp.domain.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
public class Config {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Map from ClassType -> ClassTypeDto
        mapper.addMappings(new PropertyMap<ClassType, ClassTypeDto>() {
            @Override
            protected void configure() {
                map().setInstructorIds(source.getInstructors().stream()
                        .map(Instructor::getId)
                        .collect(Collectors.toSet()));
                map().setRoomIds(source.getRooms().stream()
                        .map(Room::getId)
                        .collect(Collectors.toSet()));
                map().setFitnessClassIds(source.getClasses().stream()
                        .map(FitnessClass::getId)
                        .collect(Collectors.toSet()));
            }
        });

        // Map from ClassTypeDto -> ClassType
        mapper.addMappings(new PropertyMap<ClassTypeDto, ClassType>() {
            @Override
            protected void configure() {
                skip(destination.getInstructors());
                skip(destination.getRooms());
                skip(destination.getClasses());
            }
        });

        // Map from FitnessClass -> FitnessClassDto
        mapper.addMappings(new PropertyMap<FitnessClass, FitnessClassDto>() {
            @Override
            protected void configure() {
                map().setInstructorId(source.getInstructor().getId());
                map().setRoomId(source.getRoom().getId());
                map().setClassTypeId(source.getClassType().getId());
                map().setTraineeIds(source.getTrainees().stream()
                        .map(Trainee::getId)
                        .collect(Collectors.toSet()));
            }
        });

        // Map from FitnessClassDto -> FitnessClass
        mapper.addMappings(new PropertyMap<FitnessClassDto, FitnessClass>() {
            @Override
            protected void configure() {
                skip(destination.getInstructor());
                skip(destination.getRoom());
                skip(destination.getClassType());
                skip(destination.getTrainees());
            }
        });

        // Map from Instructor -> InstructorDto
        mapper.addMappings(new PropertyMap<Instructor, InstructorDto>() {
            @Override
            protected void configure() {
                map().setClassTypeIds(source.getSpecializations().stream()
                        .map(ClassType::getId)
                        .collect(Collectors.toSet()));
                map().setFitnessClassIds(source.getClasses().stream()
                        .map(FitnessClass::getId)
                        .collect(Collectors.toSet()));
            }
        });

        // Map from InstructorDto -> Instructor
        mapper.addMappings(new PropertyMap<InstructorDto, Instructor>() {
            @Override
            protected void configure() {
                skip(destination.getSpecializations());
                skip(destination.getClasses());
            }
        });

        // Map from Room -> RoomDto
        mapper.addMappings(new PropertyMap<Room, RoomDto>() {
            @Override
            protected void configure() {
                map().setFitnessClassIds(source.getClasses().stream()
                        .map(FitnessClass::getId)
                        .collect(Collectors.toSet()));
                map().setClassTypeIds(source.getClassTypes().stream()
                        .map(ClassType::getId)
                        .collect(Collectors.toSet()));
            }
        });

        // Map from RoomDto -> Room
        mapper.addMappings(new PropertyMap<RoomDto, Room>() {
            @Override
            protected void configure() {
                skip(destination.getClasses());
                skip(destination.getClassTypes());
            }
        });

        // Map from Trainee -> TraineeDto
        mapper.addMappings(new PropertyMap<Trainee, TraineeDto>() {
            @Override
            protected void configure() {
                map().setFitnessClassIds(source.getClasses().stream()
                        .map(FitnessClass::getId)
                        .collect(Collectors.toSet()));
            }
        });

        // Map from TraineeDto -> Trainee
        mapper.addMappings(new PropertyMap<TraineeDto, Trainee>() {
            @Override
            protected void configure() {
                skip(destination.getClasses());
            }
        });

        return mapper;
    }
}