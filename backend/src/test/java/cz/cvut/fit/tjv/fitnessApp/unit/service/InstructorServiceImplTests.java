package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServiceImplTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private ClassTypeRepository classTypeRepository;

    @Mock
    private FitnessClassRepository fitnessClassRepository;

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
        // Arrange
        // Mock new instructor to be created
        Instructor newInstructor = new Instructor();
        newInstructor.setName("Jane");
        newInstructor.setSurname("Smith");
        newInstructor.setBirthDate(LocalDate.of(1985, 5, 20));

        // Mock ClassType association
        ClassType mockClassType = new ClassType();
        mockClassType.setId(1L);

        newInstructor.setSpecializations(List.of(mockClassType));

        // Mock FitnessClass association
        FitnessClass mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(2L);

        newInstructor.setClasses(List.of(mockFitnessClass));

        // Mock repositories
        when(classTypeRepository.findById(1L)).thenReturn(Optional.of(mockClassType));
        when(fitnessClassRepository.findById(2L)).thenReturn(Optional.of(mockFitnessClass));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(mockInstructor);

        // Act
        Instructor result = instructorService.create(newInstructor);

        // Assert
        assertNotNull(result);
        assertEquals(mockInstructor.getId(), result.getId());
        assertEquals(mockInstructor.getName(), result.getName());
        assertEquals(mockInstructor.getSurname(), result.getSurname());
        assertEquals(mockInstructor.getSpecializations().size(), result.getSpecializations().size());
        assertEquals(mockInstructor.getClasses().size(), result.getClasses().size());

        // Verify persistence
        verify(instructorRepository).save(newInstructor);
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
        // Arrange
        // Mock existing instructor
        Instructor mockExistingInstructor = new Instructor();
        mockExistingInstructor.setId(1L);
        mockExistingInstructor.setName("John");
        mockExistingInstructor.setSurname("Doe");
        mockExistingInstructor.setBirthDate(LocalDate.of(1980, 1, 1));
        mockExistingInstructor.setSpecializations(new ArrayList<>());
        mockExistingInstructor.setClasses(new ArrayList<>());

        // Mock updated instructor
        Instructor updatedInstructor = new Instructor();
        updatedInstructor.setName("Jane");
        updatedInstructor.setSurname("Smith");
        updatedInstructor.setBirthDate(LocalDate.of(1985, 5, 20));

        // Mock updated associations
        ClassType mockClassType = new ClassType();
        mockClassType.setId(1L);
        updatedInstructor.setSpecializations(List.of(mockClassType));

        FitnessClass mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(2L);
        updatedInstructor.setClasses(List.of(mockFitnessClass));

        // Mock repositories
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(mockExistingInstructor));
        when(classTypeRepository.findById(1L)).thenReturn(Optional.of(mockClassType));
        when(fitnessClassRepository.findById(2L)).thenReturn(Optional.of(mockFitnessClass));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(mockExistingInstructor);

        // Act
        instructorService.update(1L, updatedInstructor);

        // Assert
        verify(instructorRepository).save(argThat(savedInstructor -> {
            // Verify basic attributes
            assertEquals("Jane", savedInstructor.getName());
            assertEquals("Smith", savedInstructor.getSurname());
            assertEquals(LocalDate.of(1985, 5, 20), savedInstructor.getBirthDate());

            // Verify specializations
            assertEquals(1, savedInstructor.getSpecializations().size());
            assertTrue(savedInstructor.getSpecializations().stream().anyMatch(ct -> ct.getId() == 1L));

            // Verify fitness classes
            assertEquals(1, savedInstructor.getClasses().size());
            assertTrue(savedInstructor.getClasses().stream().anyMatch(fc -> fc.getId() == 2L));

            // Verify inverse relationships
            assertTrue(mockClassType.getInstructors().contains(savedInstructor));
            assertEquals(savedInstructor, mockFitnessClass.getInstructor());

            return true;
        }));

        // Verify inverse updates on associations
        assertTrue(mockClassType.getInstructors().contains(mockExistingInstructor));
        assertEquals(mockExistingInstructor, mockFitnessClass.getInstructor());
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(instructorRepository.findById(1L)).thenThrow(new IllegalArgumentException());

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