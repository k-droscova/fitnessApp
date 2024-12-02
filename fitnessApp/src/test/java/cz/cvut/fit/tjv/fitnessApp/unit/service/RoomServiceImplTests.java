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

import java.time.LocalDate;
import java.time.LocalTime;
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
        Room room = new Room();
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);

        Room result = roomService.create(room);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(50, result.getMaxCapacity());
        verify(roomRepository).save(room);
    }

    @Test
    void create_ThrowsException_WhenIdExists() {
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

    @Test
    void findAvailableRooms_ShouldReturnRooms_WhenClassTypeIdIsNull() {
        when(roomRepository.findAvailableRoomsByOptionalClassType(null, LocalDate.of(2024, 12, 1), LocalTime.of(10, 0)))
                .thenReturn(List.of(mockRoom));

        List<Room> result = roomService.findAvailableRooms(Optional.empty(), LocalDate.of(2024, 12, 1), LocalTime.of(10, 0));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(50, result.get(0).getMaxCapacity());
    }

    @Test
    void findAvailableRooms_ShouldReturnRooms_WhenClassTypeIdIsProvided() {
        when(roomRepository.findAvailableRoomsByOptionalClassType(1L, LocalDate.of(2024, 12, 1), LocalTime.of(10, 0)))
                .thenReturn(List.of(mockRoom));

        List<Room> result = roomService.findAvailableRooms(Optional.of(1L), LocalDate.of(2024, 12, 1), LocalTime.of(10, 0));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(50, result.get(0).getMaxCapacity());
    }

    @Test
    void findAvailableRooms_ShouldReturnEmptyList_WhenNoRoomsAvailable() {
        when(roomRepository.findAvailableRoomsByOptionalClassType(1L, LocalDate.of(2024, 12, 1), LocalTime.of(10, 0)))
                .thenReturn(List.of());

        List<Room> result = roomService.findAvailableRooms(Optional.of(1L), LocalDate.of(2024, 12, 1), LocalTime.of(10, 0));

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}