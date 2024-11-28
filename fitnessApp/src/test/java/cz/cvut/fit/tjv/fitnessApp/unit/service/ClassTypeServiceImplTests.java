package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.service.ClassTypeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassTypeServiceImplTest {

    @Mock
    private ClassTypeRepository classTypeRepository;

    @InjectMocks
    private ClassTypeServiceImpl classTypeService;

    private ClassType mockClassType;

    @BeforeEach
    void setUp() {
        mockClassType = new ClassType();
        mockClassType.setId(1);
        mockClassType.setName("Yoga");
    }

    @AfterEach
    void tearDown() {
        mockClassType = null;
    }

    @Test
    void create_Successful() {
        when(classTypeRepository.existsById(1)).thenReturn(false);
        when(classTypeRepository.save(mockClassType)).thenReturn(mockClassType);

        ClassType result = classTypeService.create(mockClassType);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Yoga", result.getName());
        verify(classTypeRepository).save(mockClassType);
    }

    @Test
    void create_ThrowsException_WhenIdExists() {
        when(classTypeRepository.existsById(1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> classTypeService.create(mockClassType));
        verify(classTypeRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(classTypeRepository.findById(1)).thenReturn(Optional.of(mockClassType));

        Optional<ClassType> result = classTypeService.readById(1);

        assertTrue(result.isPresent());
        assertEquals("Yoga", result.get().getName());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(classTypeRepository.findById(1)).thenReturn(Optional.empty());

        Optional<ClassType> result = classTypeService.readById(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(classTypeRepository.findAll()).thenReturn(List.of(mockClassType));

        Iterable<ClassType> result = classTypeService.readAll();

        assertNotNull(result);
        assertEquals(1, ((List<ClassType>) result).size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(classTypeRepository.findAll()).thenReturn(List.of());

        Iterable<ClassType> result = classTypeService.readAll();

        assertNotNull(result);
        assertEquals(0, ((List<ClassType>) result).size());
    }

    @Test
    void update_Successful() {
        when(classTypeRepository.existsById(1)).thenReturn(true);
        when(classTypeRepository.save(mockClassType)).thenReturn(mockClassType);

        classTypeService.update(1, mockClassType);

        verify(classTypeRepository).save(mockClassType);
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(classTypeRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> classTypeService.update(1, mockClassType));
        verify(classTypeRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(classTypeRepository.existsById(1)).thenReturn(true);

        classTypeService.deleteById(1);

        verify(classTypeRepository).deleteById(1);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(classTypeRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> classTypeService.deleteById(1));
        verify(classTypeRepository, never()).deleteById(any());
    }

    @Test
    void findInstructorsByClassType_ReturnsInstructors() {
        Instructor instructor = new Instructor();
        instructor.setId(10);
        mockClassType.setInstructors(Set.of(instructor));

        when(classTypeRepository.findById(1)).thenReturn(Optional.of(mockClassType));

        Set<Instructor> result = classTypeService.findInstructorsByClassType(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(instructor));
    }

    @Test
    void findInstructorsByClassType_ReturnsEmpty_WhenClassTypeNotFound() {
        when(classTypeRepository.findById(1)).thenReturn(Optional.empty());

        Set<Instructor> result = classTypeService.findInstructorsByClassType(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void readAllByName_ReturnsMatchingEntities() {
        when(classTypeRepository.findByNameContainingIgnoreCase("Yoga")).thenReturn(new HashSet<>(List.of(mockClassType)));

        Set<ClassType> result = classTypeService.readAllByName("Yoga");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readAllByName_ReturnsEmpty_WhenNoMatches() {
        when(classTypeRepository.findByNameContainingIgnoreCase("Zumba")).thenReturn(new HashSet<>());

        Set<ClassType> result = classTypeService.readAllByName("Zumba");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}