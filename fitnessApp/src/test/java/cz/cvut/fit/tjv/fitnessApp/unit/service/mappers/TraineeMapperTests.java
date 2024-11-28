package cz.cvut.fit.tjv.fitnessApp.unit.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.TraineeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.TraineeMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeMapperTest {

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @InjectMocks
    private TraineeMapper traineeMapper;

    private Trainee mockTrainee;
    private FitnessClass mockFitnessClass;
    private TraineeDto mockTraineeDto;

    @BeforeEach
    void setUp() {
        // Mock Trainee
        mockTrainee = new Trainee();
        mockTrainee.setId(1);
        mockTrainee.setEmail("test@example.com");
        mockTrainee.setName("John");
        mockTrainee.setSurname("Doe");

        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(10);
        mockTrainee.setClasses(Set.of(mockFitnessClass));

        // Mock DTO
        mockTraineeDto = new TraineeDto();
        mockTraineeDto.setId(1);
        mockTraineeDto.setEmail("test@example.com");
        mockTraineeDto.setName("John");
        mockTraineeDto.setSurname("Doe");
        mockTraineeDto.setFitnessClassIds(Set.of(10));
    }

    @AfterEach
    void tearDown() {
        mockTrainee = null;
        mockFitnessClass = null;
        mockTraineeDto = null;
    }

    @Test
    void convertToEntity_Successful() {
        when(fitnessClassRepository.findById(10)).thenReturn(Optional.of(mockFitnessClass));

        Trainee result = traineeMapper.convertToEntity(mockTraineeDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals(1, result.getClasses().size());
        assertTrue(result.getClasses().contains(mockFitnessClass));
        verify(fitnessClassRepository).findById(10);
    }

    @Test
    void convertToEntity_ThrowsException_WhenFitnessClassNotFound() {
        when(fitnessClassRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> traineeMapper.convertToEntity(mockTraineeDto));
        verify(fitnessClassRepository).findById(10);
    }

    @Test
    void convertToEntity_ReturnsEmptyCollections_WhenIDsAreEmpty() {
        mockTraineeDto.setFitnessClassIds(Collections.emptySet());

        Trainee result = traineeMapper.convertToEntity(mockTraineeDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertTrue(result.getClasses().isEmpty());
        verifyNoInteractions(fitnessClassRepository);
    }

    @Test
    void convertToEntity_ReturnsEmptyEntity_WhenDtoIsEmpty() {
        TraineeDto emptyDto = new TraineeDto();
        Trainee result = traineeMapper.convertToEntity(emptyDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getEmail());
        assertNull(result.getName());
        assertNull(result.getSurname());
        assertTrue(result.getClasses().isEmpty());
    }

    @Test
    void convertToDto_Successful() {
        TraineeDto result = traineeMapper.convertToDto(mockTrainee);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        assertEquals(Set.of(10), result.getFitnessClassIds());
    }

    @Test
    void convertToDto_ReturnsDtoWithEmptyCollections_WhenEntityHasEmptyCollections() {
        mockTrainee.setClasses(Collections.emptySet());

        TraineeDto result = traineeMapper.convertToDto(mockTrainee);

        assertNotNull(result);
        assertTrue(result.getFitnessClassIds().isEmpty());
    }

    @Test
    void convertToDto_ThrowsException_WhenEntityIsNull() {
        assertThrows(NullPointerException.class, () -> traineeMapper.convertToDto(null));
    }

    @Test
    void convertManyToDto_Successful() {
        List<TraineeDto> result = traineeMapper.convertManyToDto(List.of(mockTrainee));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("test@example.com", result.get(0).getEmail());
        assertEquals("John", result.get(0).getName());
        assertEquals("Doe", result.get(0).getSurname());
        assertEquals(Set.of(10), result.get(0).getFitnessClassIds());
    }

    @Test
    void convertManyToDto_ReturnsEmptyList_WhenInputIsEmpty() {
        List<TraineeDto> result = traineeMapper.convertManyToDto(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}