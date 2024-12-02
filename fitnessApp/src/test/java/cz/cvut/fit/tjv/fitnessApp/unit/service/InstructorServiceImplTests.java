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

import java.time.LocalDate;
import java.time.LocalTime;
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
        mockInstructor.setId(1L);
        mockInstructor.setName("John");
        mockInstructor.setSurname("Doe");
    }

    @AfterEach
    void tearDown() {
        mockInstructor = null;
    }

    @Test
    void create_Successful() {
        Instructor instructor = new Instructor();
        when(instructorRepository.save(any(Instructor.class))).thenReturn(mockInstructor);

        Instructor result = instructorService.create(instructor);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getSurname());
        verify(instructorRepository).save(instructor);
    }

    @Test
    void create_ThrowsException_WhenIdExists() {
        assertThrows(IllegalArgumentException.class, () -> instructorService.create(mockInstructor));
        verify(instructorRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(mockInstructor));

        Optional<Instructor> result = instructorService.readById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName());
        assertEquals("Doe", result.get().getSurname());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(instructorRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Instructor> result = instructorService.readById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(instructorRepository.findAll()).thenReturn(List.of(mockInstructor));

        List<Instructor> result = instructorService.readAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(instructorRepository.findAll()).thenReturn(List.of());

        List<Instructor> result = instructorService.readAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void update_Successful() {
        when(instructorRepository.existsById(1L)).thenReturn(true);
        when(instructorRepository.save(mockInstructor)).thenReturn(mockInstructor);

        instructorService.update(1L, mockInstructor);

        verify(instructorRepository).save(mockInstructor);
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(instructorRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> instructorService.update(1L, mockInstructor));
        verify(instructorRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(instructorRepository.existsById(1L)).thenReturn(true);

        instructorService.deleteById(1L);

        verify(instructorRepository).deleteById(1L);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(instructorRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> instructorService.deleteById(1L));
        verify(instructorRepository, never()).deleteById(any());
    }

    @Test
    void readAllByName_ShouldReturnMatchingInstructors() {
        when(instructorRepository.findInstructorByNameStartingWithIgnoreCase("Jo"))
                .thenReturn(List.of(mockInstructor));

        List<Instructor> result = instructorService.readAllByName("Jo");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void readAllBySurname_ShouldReturnMatchingInstructors() {
        when(instructorRepository.findInstructorsBySurnameStartingWithIgnoreCase("Do"))
                .thenReturn(List.of(mockInstructor));

        List<Instructor> result = instructorService.readAllBySurname("Do");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getSurname());
    }

    @Test
    void readAllByNameOrSurname_ShouldReturnMatchingInstructors() {
        when(instructorRepository.findInstructorsByNameOrSurnameStartingWithIgnoreCase("Jo"))
                .thenReturn(List.of(mockInstructor));

        List<Instructor> result = instructorService.readAllByNameOrSurname("Jo");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void findAvailableInstructors_ShouldReturnInstructors_WhenClassTypeIdIsNull() {
        when(instructorRepository.findAvailableInstructorsByOptionalClassType(null, LocalDate.of(2024, 12, 1), LocalTime.of(10, 0)))
                .thenReturn(List.of(mockInstructor));

        List<Instructor> result = instructorService.findAvailableInstructors(Optional.empty(), LocalDate.of(2024, 12, 1), LocalTime.of(10, 0));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void findAvailableInstructors_ShouldReturnInstructors_WhenClassTypeIdIsProvided() {
        when(instructorRepository.findAvailableInstructorsByOptionalClassType(1L, LocalDate.of(2024, 12, 1), LocalTime.of(10, 0)))
                .thenReturn(List.of(mockInstructor));

        List<Instructor> result = instructorService.findAvailableInstructors(Optional.of(1L), LocalDate.of(2024, 12, 1), LocalTime.of(10, 0));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void findAvailableInstructors_ShouldReturnEmptyList_WhenNoInstructorsAvailable() {
        when(instructorRepository.findAvailableInstructorsByOptionalClassType(1L, LocalDate.of(2024, 12, 1), LocalTime.of(10, 0)))
                .thenReturn(List.of());

        List<Instructor> result = instructorService.findAvailableInstructors(Optional.of(1L), LocalDate.of(2024, 12, 1), LocalTime.of(10, 0));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}