package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.service.InstructorServiceImpl;
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
class InstructorServiceImplTest {

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private InstructorServiceImpl instructorService;

    private Instructor mockInstructor;

    @BeforeEach
    void setUp() {
        mockInstructor = new Instructor();
        mockInstructor.setId(1);
        mockInstructor.setName("John");
        mockInstructor.setSurname("Doe");
    }

    @AfterEach
    void tearDown() {
        mockInstructor = null;
    }

    @Test
    void create_Successful() {
        when(instructorRepository.existsById(1)).thenReturn(false);
        when(instructorRepository.save(mockInstructor)).thenReturn(mockInstructor);

        Instructor result = instructorService.create(mockInstructor);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        verify(instructorRepository).save(mockInstructor);
    }

    @Test
    void create_ThrowsException_WhenIdExists() {
        when(instructorRepository.existsById(1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> instructorService.create(mockInstructor));
        verify(instructorRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(instructorRepository.findById(1)).thenReturn(Optional.of(mockInstructor));

        Optional<Instructor> result = instructorService.readById(1);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName());
        assertEquals("Doe", result.get().getSurname());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(instructorRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Instructor> result = instructorService.readById(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(instructorRepository.findAll()).thenReturn(List.of(mockInstructor));

        Iterable<Instructor> result = instructorService.readAll();

        assertNotNull(result);
        assertEquals(1, ((List<Instructor>) result).size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(instructorRepository.findAll()).thenReturn(List.of());

        Iterable<Instructor> result = instructorService.readAll();

        assertNotNull(result);
        assertEquals(0, ((List<Instructor>) result).size());
    }

    @Test
    void update_Successful() {
        when(instructorRepository.existsById(1)).thenReturn(true);
        when(instructorRepository.save(mockInstructor)).thenReturn(mockInstructor);

        instructorService.update(1, mockInstructor);

        verify(instructorRepository).save(mockInstructor);
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(instructorRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> instructorService.update(1, mockInstructor));
        verify(instructorRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(instructorRepository.existsById(1)).thenReturn(true);

        instructorService.deleteById(1);

        verify(instructorRepository).deleteById(1);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(instructorRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> instructorService.deleteById(1));
        verify(instructorRepository, never()).deleteById(any());
    }
}
