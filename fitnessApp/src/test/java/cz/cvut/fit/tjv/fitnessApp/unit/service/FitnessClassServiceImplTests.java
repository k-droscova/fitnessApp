package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.service.FitnessClassServiceImpl;
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
class FitnessClassServiceImplTest {

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @InjectMocks
    private FitnessClassServiceImpl fitnessClassService;

    private FitnessClass mockFitnessClass;

    @BeforeEach
    void setUp() {
        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(1);
        mockFitnessClass.setCapacity(20);
    }

    @AfterEach
    void tearDown() {
        mockFitnessClass = null;
    }

    @Test
    void create_Successful() {
        when(fitnessClassRepository.existsById(1)).thenReturn(false);
        when(fitnessClassRepository.save(mockFitnessClass)).thenReturn(mockFitnessClass);

        FitnessClass result = fitnessClassService.create(mockFitnessClass);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(20, result.getCapacity());
        verify(fitnessClassRepository).save(mockFitnessClass);
    }

    @Test
    void create_ThrowsException_WhenIdExists() {
        when(fitnessClassRepository.existsById(1)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.create(mockFitnessClass));
        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(fitnessClassRepository.findById(1)).thenReturn(Optional.of(mockFitnessClass));

        Optional<FitnessClass> result = fitnessClassService.readById(1);

        assertTrue(result.isPresent());
        assertEquals(20, result.get().getCapacity());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(fitnessClassRepository.findById(1)).thenReturn(Optional.empty());

        Optional<FitnessClass> result = fitnessClassService.readById(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(fitnessClassRepository.findAll()).thenReturn(List.of(mockFitnessClass));

        Iterable<FitnessClass> result = fitnessClassService.readAll();

        assertNotNull(result);
        assertEquals(1, ((List<FitnessClass>) result).size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(fitnessClassRepository.findAll()).thenReturn(List.of());

        Iterable<FitnessClass> result = fitnessClassService.readAll();

        assertNotNull(result);
        assertEquals(0, ((List<FitnessClass>) result).size());
    }

    @Test
    void update_Successful() {
        when(fitnessClassRepository.existsById(1)).thenReturn(true);
        when(fitnessClassRepository.save(mockFitnessClass)).thenReturn(mockFitnessClass);

        fitnessClassService.update(1, mockFitnessClass);

        verify(fitnessClassRepository).save(mockFitnessClass);
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(fitnessClassRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.update(1, mockFitnessClass));
        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(fitnessClassRepository.existsById(1)).thenReturn(true);

        fitnessClassService.deleteById(1);

        verify(fitnessClassRepository).deleteById(1);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(fitnessClassRepository.existsById(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.deleteById(1));
        verify(fitnessClassRepository, never()).deleteById(any());
    }
}
