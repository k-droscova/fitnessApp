package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import cz.cvut.fit.tjv.fitnessApp.service.RoomServiceImpl;
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
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room mockRoom;

    @BeforeEach
    void setUp() {
        mockRoom = new Room();
        mockRoom.setId(1L);
        mockRoom.setMaxCapacity(50);
    }

    @AfterEach
    void tearDown() {
        mockRoom = null;
    }

    @Test
    void create_Successful() {
        when(roomRepository.existsById(1L)).thenReturn(false);
        when(roomRepository.save(mockRoom)).thenReturn(mockRoom);

        Room result = roomService.create(mockRoom);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(50, result.getMaxCapacity());
        verify(roomRepository).save(mockRoom);
    }

    @Test
    void create_ThrowsException_WhenIdExists() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> roomService.create(mockRoom));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));

        Optional<Room> result = roomService.readById(1L);

        assertTrue(result.isPresent());
        assertEquals(50, result.get().getMaxCapacity());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Room> result = roomService.readById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(roomRepository.findAll()).thenReturn(List.of(mockRoom));

        List<Room> result = roomService.readAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(roomRepository.findAll()).thenReturn(List.of());

        List<Room> result = roomService.readAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void update_Successful() {
        when(roomRepository.existsById(1L)).thenReturn(true);
        when(roomRepository.save(mockRoom)).thenReturn(mockRoom);

        roomService.update(1L, mockRoom);

        verify(roomRepository).save(mockRoom);
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(roomRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> roomService.update(1L, mockRoom));
        verify(roomRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        roomService.deleteById(1L);

        verify(roomRepository).deleteById(1L);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(roomRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> roomService.deleteById(1L));
        verify(roomRepository, never()).deleteById(any());
    }
}
