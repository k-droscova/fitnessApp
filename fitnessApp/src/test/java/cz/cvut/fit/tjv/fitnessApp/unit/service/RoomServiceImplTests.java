package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
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

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @Mock
    private ClassTypeRepository classTypeRepository;

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
        // Arrange
        Room updatedRoom = new Room();
        updatedRoom.setMaxCapacity(50);
        ClassType classType = new ClassType();
        classType.setId(1L);
        classType.setName("Yoga");
        updatedRoom.setClassTypes(List.of(classType));
        FitnessClass fitnessClass = new FitnessClass();
        fitnessClass.setId(2L);
        fitnessClass.setClassType(classType);
        fitnessClass.setDate(LocalDate.now());
        fitnessClass.setTime(LocalTime.now());
        fitnessClass.setCapacity(10);
        updatedRoom.setClasses(List.of(fitnessClass));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));
        when(classTypeRepository.findById(1L)).thenReturn(Optional.of(classType));
        when(fitnessClassRepository.findById(2L)).thenReturn(Optional.of(fitnessClass));
        when(roomRepository.save(any(Room.class))).thenReturn(mockRoom);

        // Act
        Room result = roomService.update(1L, updatedRoom);

        // Assert
        assertNotNull(result);
        assertEquals(50, result.getMaxCapacity());
        verify(classTypeRepository).findById(1L);
        verify(fitnessClassRepository).findById(2L);
        verify(roomRepository).save(mockRoom);

        // Verify associations
        assertEquals(1, mockRoom.getClassTypes().size());
        assertEquals(1L, mockRoom.getClassTypes().get(0).getId());
        assertEquals(1, mockRoom.getClasses().size());
        assertEquals(2L, mockRoom.getClasses().get(0).getId());
    }

    @Test
    void update_ThrowsException_WhenRoomNotFound() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> roomService.update(1L, mockRoom));

        // Verify
        verify(roomRepository, never()).save(any());
        verify(classTypeRepository, never()).findById(anyLong());
        verify(fitnessClassRepository, never()).findById(anyLong());
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