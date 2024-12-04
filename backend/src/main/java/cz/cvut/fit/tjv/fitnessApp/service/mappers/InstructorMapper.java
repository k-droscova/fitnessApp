package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.instructor.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstructorMapper implements EntityMapper<Instructor, InstructorDto> {

    private final ClassTypeRepository classTypeRepository;
    private final FitnessClassRepository fitnessClassRepository;

    public InstructorMapper(ClassTypeRepository classTypeRepository,
                            FitnessClassRepository fitnessClassRepository) {
        this.classTypeRepository = classTypeRepository;
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    public Instructor convertToEntity(InstructorDto instructorDto) {
        Instructor instructor = new Instructor();
        instructor.setId(instructorDto.getId());
        instructor.setName(instructorDto.getName());
        instructor.setSurname(instructorDto.getSurname());
        instructor.setBirthDate(instructorDto.getBirthDate());

        // Map ClassType IDs to entities
        if (instructorDto.getClassTypeIds() != null) {
            List<ClassType> specializations = instructorDto.getClassTypeIds().stream()
                    .map(id -> classTypeRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("ClassType not found for ID: " + id)))
                    .collect(Collectors.toList());
            instructor.setSpecializations(specializations);
        }

        // Map FitnessClass IDs to entities
        if (instructorDto.getFitnessClassIds() != null) {
            List<FitnessClass> classes = instructorDto.getFitnessClassIds().stream()
                    .map(id -> fitnessClassRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found for ID: " + id)))
                    .collect(Collectors.toList());
            instructor.setClasses(classes);
        }

        return instructor;
    }

    @Override
    public InstructorDto convertToDto(Instructor instructor) {
        InstructorDto instructorDto = new InstructorDto();
        instructorDto.setId(instructor.getId());
        instructorDto.setName(instructor.getName());
        instructorDto.setSurname(instructor.getSurname());
        instructorDto.setBirthDate(instructor.getBirthDate());

        // Map ClassType entities to IDs
        if (instructor.getSpecializations() != null) {
            List<Long> classTypeIds = instructor.getSpecializations().stream()
                    .map(ClassType::getId)
                    .collect(Collectors.toList());
            instructorDto.setClassTypeIds(classTypeIds);
        }

        // Map FitnessClass entities to IDs
        if (instructor.getClasses() != null) {
            List<Long> fitnessClassIds = instructor.getClasses().stream()
                    .map(FitnessClass::getId)
                    .collect(Collectors.toList());
            instructorDto.setFitnessClassIds(fitnessClassIds);
        }

        return instructorDto;
    }

    @Override
    public List<InstructorDto> convertManyToDto(List<Instructor> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}