package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
import cz.cvut.fit.tjv.fitnessApp.service.TraineeServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee mockTrainee;

    @BeforeEach
    void setUp() {
        mockTrainee = new Trainee();
        mockTrainee.setId(1L);
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
        Trainee trainee = new Trainee();
        when(traineeRepository.save(any(Trainee.class))).thenReturn(mockTrainee);

        Trainee result = traineeService.create(trainee);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("trainee@example.com", result.getEmail());
        assertEquals("Jane", result.getName());
        assertEquals("Doe", result.getSurname());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void create_ThrowsException_WhenIdIsNotNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.create(mockTrainee));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(mockTrainee));

        Optional<Trainee> result = traineeService.readById(1L);

        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getName());
        assertEquals("Doe", result.get().getSurname());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.readById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(traineeRepository.findAll()).thenReturn(List.of(mockTrainee));

        List<Trainee> result = traineeService.readAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(traineeRepository.findAll()).thenReturn(List.of());

        List<Trainee> result = traineeService.readAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void update_Successful() {
        // Arrange
        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setEmail("newemail@example.com");
        updatedTrainee.setName("UpdatedName");
        updatedTrainee.setSurname("UpdatedSurname");

        // Mock existing trainee
        Trainee existingTrainee = new Trainee();
        existingTrainee.setId(1L);
        existingTrainee.setEmail("oldemail@example.com");
        existingTrainee.setName("OldName");
        existingTrainee.setSurname("OldSurname");

        // Mock fitness classes
        FitnessClass existingClass = new FitnessClass();
        existingClass.setId(1L);

        FitnessClass newClass = new FitnessClass();
        newClass.setId(2L);

        updatedTrainee.setClasses(List.of(newClass));

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(existingTrainee));
        when(fitnessClassRepository.findById(2L)).thenReturn(Optional.of(newClass));
        when(traineeRepository.save(existingTrainee)).thenReturn(existingTrainee);

        // Act
        Trainee result = traineeService.update(1L, updatedTrainee);

        // Assert
        assertNotNull(result);
        assertEquals("newemail@example.com", existingTrainee.getEmail());
        assertEquals("UpdatedName", existingTrainee.getName());
        assertEquals("UpdatedSurname", existingTrainee.getSurname());
        assertEquals(1, existingTrainee.getClasses().size());
        assertEquals(2L, existingTrainee.getClasses().get(0).getId());
        verify(traineeRepository).save(existingTrainee);
    }

    @Test
    void update_ThrowsException_WhenTraineeDoesNotExist() {
        // Arrange
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> traineeService.update(1L, new Trainee()));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(traineeRepository.existsById(1L)).thenReturn(true);

        traineeService.deleteById(1L);

        verify(traineeRepository).deleteById(1L);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(traineeRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> traineeService.deleteById(1L));
        verify(traineeRepository, never()).deleteById(any());
    }

    @Test
    void findTraineesByFitnessClassId_ShouldReturnTrainees_WhenFitnessClassExists() {
        when(traineeRepository.findByFitnessClassId(1L)).thenReturn(List.of(mockTrainee));

        List<Trainee> result = traineeService.findTraineesByFitnessClassId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jane", result.get(0).getName());
        assertEquals("Doe", result.get(0).getSurname());
    }

    @Test
    void findTraineesByFitnessClassId_ShouldReturnEmptyList_WhenNoTraineesFound() {
        when(traineeRepository.findByFitnessClassId(1L)).thenReturn(Collections.emptyList());

        List<Trainee> result = traineeService.findTraineesByFitnessClassId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findTraineesByFitnessClassId_ShouldThrowException_WhenFitnessClassIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.findTraineesByFitnessClassId(null));

        verify(traineeRepository, never()).findByFitnessClassId(any());
    }

    @Test
    void findTraineesByName_ShouldReturnTrainees_WhenMatching() {
        // Arrange
        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        trainee2.setName("John");
        trainee2.setSurname("Doe");

        when(traineeRepository.findByNameOrSurnameStartingWithIgnoreCase("doe"))
                .thenReturn(List.of(mockTrainee, trainee2));

        // Act
        List<Trainee> result = traineeService.findTraineesByName("doe");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size(), "Expected 2 trainees with matching name or surname");
        assertTrue(result.stream().anyMatch(t -> "Jane".equals(t.getName()) && "Doe".equals(t.getSurname())),
                "Expected trainee Jane Doe in the results");
        assertTrue(result.stream().anyMatch(t -> "John".equals(t.getName()) && "Doe".equals(t.getSurname())),
                "Expected trainee John Doe in the results");
    }

    @Test
    void findTraineesByName_ShouldReturnEmptyList_WhenNoMatchingTrainees() {
        // Arrange
        when(traineeRepository.findByNameOrSurnameStartingWithIgnoreCase("xyz"))
                .thenReturn(Collections.emptyList());

        // Act
        List<Trainee> result = traineeService.findTraineesByName("xyz");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Expected no trainees to match the input 'xyz'");
    }

    @Test
    void findTraineesByName_ShouldThrowException_WhenInputIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> traineeService.findTraineesByName(null),
                "Expected IllegalArgumentException for null input");
        verify(traineeRepository, never()).findByNameOrSurnameStartingWithIgnoreCase(any());
    }
}