package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InstructorMapper implements EntityMapper<Instructor, InstructorDto> {

    private final ClassTypeRepository classTypeRepository;
    private final FitnessClassRepository fitnessClassRepository;
    private final ModelMapper modelMapper;

    public InstructorMapper(ClassTypeRepository classTypeRepository,
                            FitnessClassRepository fitnessClassRepository,
                            ModelMapper modelMapper) {
        this.classTypeRepository = classTypeRepository;
        this.fitnessClassRepository = fitnessClassRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Instructor convertToEntity(InstructorDto instructorDto) {
        Instructor instructor = modelMapper.map(instructorDto, Instructor.class);

        // Map ClassType IDs
        if (instructorDto.getClassTypeIds() != null) {
            Set<ClassType> specializations = instructorDto.getClassTypeIds().stream()
                    .map(id -> classTypeRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("ClassType not found for ID: " + id)))
                    .collect(Collectors.toSet());
            instructor.setSpecializations(specializations);
        }

        // Map FitnessClass IDs
        if (instructorDto.getFitnessClassIds() != null) {
            Set<FitnessClass> classes = instructorDto.getFitnessClassIds().stream()
                    .map(id -> fitnessClassRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found for ID: " + id)))
                    .collect(Collectors.toSet());
            instructor.setClasses(classes);
        }

        return instructor;
    }

    @Override
    public InstructorDto convertToDto(Instructor instructor) {
        return modelMapper.map(instructor, InstructorDto.class);
    }

    @Override
    public List<InstructorDto> convertManyToDto(List<Instructor> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}