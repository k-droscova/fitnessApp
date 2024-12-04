package cz.cvut.fit.tjv.fitnessApp.unit.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.instructor.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.InstructorMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorMapperTest {

    @Mock
    private ClassTypeRepository classTypeRepository;

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @InjectMocks
    private InstructorMapper instructorMapper;

    private Instructor mockInstructor;
    private ClassType mockClassType;
    private FitnessClass mockFitnessClass;
    private InstructorDto mockInstructorDto;

    @BeforeEach
    void setUp() {
        // Mock Instructor
        mockInstructor = new Instructor();
        mockInstructor.setId(1L);
        mockInstructor.setName("John");
        mockInstructor.setSurname("Doe");
        mockInstructor.setBirthDate(LocalDate.of(1985, 5, 15));

        mockClassType = new ClassType();
        mockClassType.setId(10L);
        mockInstructor.setSpecializations(List.of(mockClassType));

        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(20L);
        mockInstructor.setClasses(List.of(mockFitnessClass));

        // Mock DTO
        mockInstructorDto = new InstructorDto();
        mockInstructorDto.setId(1L);
        mockInstructorDto.setName("John");
        mockInstructorDto.setSurname("Doe");
        mockInstructorDto.setBirthDate(LocalDate.of(1985, 5, 15));
        mockInstructorDto.setClassTypeIds(List.of(10L));
        mockInstructorDto.setFitnessClassIds(List.of(20L));
    }

    @AfterEach
    void tearDown() {
        mockInstructor = null;
        mockClassType = null;
        mockFitnessClass = null;
        mockInstructorDto = null;
    }

    @Test
    void convertToEntity_Successful() {
        when(classTypeRepository.findById(10L)).thenReturn(Optional.of(mockClassType));
        when(fitnessClassRepository.findById(20L)).thenReturn(Optional.of(mockFitnessClass));

        Instructor result = instructorMapper.convertToEntity(mockInstructorDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals(LocalDate.of(1985, 5, 15), result.getBirthDate());
        assertEquals(1, result.getSpecializations().size());
        assertTrue(result.getSpecializations().contains(mockClassType));
        assertEquals(1, result.getClasses().size());
        assertTrue(result.getClasses().contains(mockFitnessClass));
    }

    @Test
    void convertToEntity_ThrowsException_WhenClassTypeNotFound() {
        when(classTypeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> instructorMapper.convertToEntity(mockInstructorDto));
    }

    @Test
    void convertToEntity_ThrowsException_WhenFitnessClassNotFound() {
        when(classTypeRepository.findById(10L)).thenReturn(Optional.of(mockClassType));
        when(fitnessClassRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> instructorMapper.convertToEntity(mockInstructorDto));
    }

    @Test
    void convertToEntity_ReturnsEmptyCollections_WhenIDsAreEmpty() {
        mockInstructorDto.setClassTypeIds(Collections.emptyList());
        mockInstructorDto.setFitnessClassIds(Collections.emptyList());

        Instructor result = instructorMapper.convertToEntity(mockInstructorDto);

        assertNotNull(result);
        assertTrue(result.getSpecializations().isEmpty());
        assertTrue(result.getClasses().isEmpty());
    }

    @Test
    void convertToEntity_ReturnsEmptyEntity_WhenDtoIsEmpty() {
        InstructorDto emptyDto = new InstructorDto();
        Instructor result = instructorMapper.convertToEntity(emptyDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getSurname());
        assertNull(result.getBirthDate());
        assertTrue(result.getSpecializations().isEmpty());
        assertTrue(result.getClasses().isEmpty());
    }

    @Test
    void convertToDto_Successful() {
        InstructorDto result = instructorMapper.convertToDto(mockInstructor);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals(LocalDate.of(1985, 5, 15), result.getBirthDate());
        assertEquals(List.of(10L), result.getClassTypeIds());
        assertEquals(List.of(20L), result.getFitnessClassIds());
    }

    @Test
    void convertToDto_ReturnsDtoWithEmptyCollections_WhenEntityHasEmptyCollections() {
        mockInstructor.setSpecializations(Collections.emptyList());
        mockInstructor.setClasses(Collections.emptyList());

        InstructorDto result = instructorMapper.convertToDto(mockInstructor);

        assertNotNull(result);
        assertTrue(result.getClassTypeIds().isEmpty());
        assertTrue(result.getFitnessClassIds().isEmpty());
    }

    @Test
    void convertManyToDto_Successful() {
        List<InstructorDto> result = instructorMapper.convertManyToDto(List.of(mockInstructor));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void convertManyToDto_ReturnsEmptyList_WhenInputIsEmpty() {
        List<InstructorDto> result = instructorMapper.convertManyToDto(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
