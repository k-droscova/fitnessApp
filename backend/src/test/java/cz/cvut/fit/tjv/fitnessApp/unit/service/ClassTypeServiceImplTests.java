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

import java.util.List;
import java.util.Optional;

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
        mockClassType.setId(1L);
        mockClassType.setName("Yoga");
    }

    @AfterEach
    void tearDown() {
        mockClassType = null;
    }

    @Test
    void create_Successful() {
        ClassType classType = new ClassType();
        classType.setName(mockClassType.getName());
        when(classTypeRepository.save(any(ClassType.class))).thenReturn(mockClassType);

        ClassType result = classTypeService.create(classType);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Yoga", result.getName());
        verify(classTypeRepository).save(classType);
    }

    @Test
    void create_ThrowsException_WhenIdIsSet() {
        assertThrows(IllegalArgumentException.class, () -> classTypeService.create(mockClassType));
        verify(classTypeRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(classTypeRepository.findById(1L)).thenReturn(Optional.of(mockClassType));

        Optional<ClassType> result = classTypeService.readById(1L);

        assertTrue(result.isPresent());
        assertEquals("Yoga", result.get().getName());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(classTypeRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ClassType> result = classTypeService.readById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(classTypeRepository.findAll()).thenReturn(List.of(mockClassType));

        List<ClassType> result = classTypeService.readAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(classTypeRepository.findAll()).thenReturn(List.of());

        List<ClassType> result = classTypeService.readAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void update_Successful() {
        when(classTypeRepository.existsById(1L)).thenReturn(true);
        when(classTypeRepository.save(mockClassType)).thenReturn(mockClassType);

        classTypeService.update(1L, mockClassType);

        verify(classTypeRepository).save(mockClassType);
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(classTypeRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> classTypeService.update(1L, mockClassType));
        verify(classTypeRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(classTypeRepository.existsById(1L)).thenReturn(true);

        classTypeService.deleteById(1L);

        verify(classTypeRepository).deleteById(1L);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(classTypeRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> classTypeService.deleteById(1L));
        verify(classTypeRepository, never()).deleteById(any());
    }

    @Test
    void findInstructorsByClassType_ReturnsInstructors() {
        Instructor instructor = new Instructor();
        instructor.setId(10L);
        mockClassType.setInstructors(List.of(instructor));

        when(classTypeRepository.findById(1L)).thenReturn(Optional.of(mockClassType));

        List<Instructor> result = classTypeService.findInstructorsById(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(instructor));
    }

    @Test
    void findInstructorsByClassType_ReturnsEmpty_WhenIdNotFound() {
        when(classTypeRepository.findById(1L)).thenReturn(Optional.empty());

        List<Instructor> result = classTypeService.findInstructorsById(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void readAllByName_ReturnsMatchingEntities() {
        ClassType classType = new ClassType();
        classType.setName("Power Yoga");
        classType.setId(2L);
        when(classTypeRepository.findByNameContainingIgnoreCase("Yoga")).thenReturn(List.of(mockClassType, classType));

        List<ClassType> result = classTypeService.readAllByName("Yoga");

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void readAllByName_ReturnsEmpty_WhenNoMatches() {
        when(classTypeRepository.findByNameContainingIgnoreCase("Zumba")).thenReturn(List.of());

        List<ClassType> result = classTypeService.readAllByName("Zumba");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}