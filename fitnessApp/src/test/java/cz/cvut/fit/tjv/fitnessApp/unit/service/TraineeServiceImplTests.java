package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
import cz.cvut.fit.tjv.fitnessApp.service.TraineeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee mockTrainee;

    @BeforeEach
    void setUp() {
        mockTrainee = new Trainee();
        mockTrainee.setId(1);
        mockTrainee.setEmail("trainee@example.com");
        mockTrainee.setName("Jane");
        mockTrainee.setSurname("Doe");
    }

    @AfterEach
    void tearDown() {
        mockTrainee = null;
    }

    @Test
    void create_Successful() {
        when(traineeRepository.existsById(1)).thenReturn(false);
        when(traineeRepository.save(mockTrainee)).thenReturn(mockTrainee);

        Trainee result = traineeService.create(mockTrainee);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("trainee@example.com", result.getEmail());
        assertEquals("Jane", result.getName());
        assertEquals("Doe", result.getSurname());
        verify(traineeRepository).save(mockTrainee);
    }

    @Test
    void create_ThrowsException_WhenIdExists() {
        when(traineeRepository.existsById(1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> traineeService.create(mockTrainee));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(traineeRepository.findById(1)).thenReturn(Optional.of(mockTrainee));

        Optional<Trainee> result = traineeService.readById(1);

        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getName());
        assertEquals("Doe", result.get().getSurname());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(traineeRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.readById(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(traineeRepository.findAll()).thenReturn(List.of(mockTrainee));

        Iterable<Trainee> result = traineeService.readAll();

        assertNotNull(result);
        assertEquals(1, ((List<Trainee>) result).size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(traineeRepository.findAll()).thenReturn(List.of());

        Iterable<Trainee> result = traineeService.readAll();

        assertNotNull(result);
        assertEquals(0, ((List<Trainee>) result).size());
    }

    @Test
    void update_Successful() {
        when(traineeRepository.existsById(1)).thenReturn(true);
        when(traineeRepository.save(mockTrainee)).thenReturn(mockTrainee);

        traineeService.update(1, mockTrainee);

        verify(traineeRepository).save(mockTrainee);
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(traineeRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> traineeService.update(1, mockTrainee));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(traineeRepository.existsById(1)).thenReturn(true);

        traineeService.deleteById(1);

        verify(traineeRepository).deleteById(1);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(traineeRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> traineeService.deleteById(1));
        verify(traineeRepository, never()).deleteById(any());
    }
}