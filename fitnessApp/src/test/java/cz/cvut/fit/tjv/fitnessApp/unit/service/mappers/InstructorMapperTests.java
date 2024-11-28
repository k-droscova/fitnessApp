package cz.cvut.fit.tjv.fitnessApp.unit.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.InstructorDto;
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
import java.util.Set;

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
        mockInstructor.setId(1);
        mockInstructor.setName("John");
        mockInstructor.setSurname("Doe");
        mockInstructor.setBirthDate(LocalDate.of(1985, 5, 15));

        mockClassType = new ClassType();
        mockClassType.setId(10);
        mockInstructor.setSpecializations(Set.of(mockClassType));

        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(20);
        mockInstructor.setClasses(Set.of(mockFitnessClass));

        // Mock DTO
        mockInstructorDto = new InstructorDto();
        mockInstructorDto.setId(1);
        mockInstructorDto.setName("John");
        mockInstructorDto.setSurname("Doe");
        mockInstructorDto.setBirthDate(LocalDate.of(1985, 5, 15));
        mockInstructorDto.setClassTypeIds(Set.of(10));
        mockInstructorDto.setFitnessClassIds(Set.of(20));
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
        when(classTypeRepository.findById(10)).thenReturn(Optional.of(mockClassType));
        when(fitnessClassRepository.findById(20)).thenReturn(Optional.of(mockFitnessClass));

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
        when(classTypeRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> instructorMapper.convertToEntity(mockInstructorDto));
    }

    @Test
    void convertToEntity_ThrowsException_WhenFitnessClassNotFound() {
        when(classTypeRepository.findById(10)).thenReturn(Optional.of(mockClassType));
        when(fitnessClassRepository.findById(20)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> instructorMapper.convertToEntity(mockInstructorDto));
    }

    @Test
    void convertToEntity_ReturnsEmptyCollections_WhenIDsAreEmpty() {
        mockInstructorDto.setClassTypeIds(Collections.emptySet());
        mockInstructorDto.setFitnessClassIds(Collections.emptySet());

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
        assertEquals(Set.of(10), result.getClassTypeIds());
        assertEquals(Set.of(20), result.getFitnessClassIds());
    }

    @Test
    void convertToDto_ReturnsDtoWithEmptyCollections_WhenEntityHasEmptyCollections() {
        mockInstructor.setSpecializations(Collections.emptySet());
        mockInstructor.setClasses(Collections.emptySet());

        InstructorDto result = instructorMapper.convertToDto(mockInstructor);

        assertNotNull(result);
        assertTrue(result.getClassTypeIds().isEmpty());
        assertTrue(result.getFitnessClassIds().isEmpty());
    }

    @Test
    void convertToDto_ThrowsException_WhenEntityIsNull() {
        assertThrows(NullPointerException.class, () -> instructorMapper.convertToDto(null));
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
