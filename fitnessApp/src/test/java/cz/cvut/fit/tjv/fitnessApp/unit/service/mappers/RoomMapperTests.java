package cz.cvut.fit.tjv.fitnessApp.unit.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.RoomMapper;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomMapperTest {

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @Mock
    private ClassTypeRepository classTypeRepository;

    @InjectMocks
    private RoomMapper roomMapper;

    private Room mockRoom;
    private FitnessClass mockFitnessClass;
    private ClassType mockClassType;
    private RoomDto mockRoomDto;

    @BeforeEach
    void setUp() {
        // Mock Room
        mockRoom = new Room();
        mockRoom.setId(1);
        mockRoom.setMaxCapacity(50);

        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(10);
        mockRoom.setClasses(Set.of(mockFitnessClass));

        mockClassType = new ClassType();
        mockClassType.setId(20);
        mockRoom.setClassTypes(Set.of(mockClassType));

        // Mock DTO
        mockRoomDto = new RoomDto();
        mockRoomDto.setId(1);
        mockRoomDto.setMaxCapacity(50);
        mockRoomDto.setFitnessClassIds(Set.of(10));
        mockRoomDto.setClassTypeIds(Set.of(20));
    }

    @AfterEach
    void tearDown() {
        mockRoom = null;
        mockFitnessClass = null;
        mockClassType = null;
        mockRoomDto = null;
    }

    @Test
    void convertToEntity_Successful() {
        when(fitnessClassRepository.findById(10)).thenReturn(Optional.of(mockFitnessClass));
        when(classTypeRepository.findById(20)).thenReturn(Optional.of(mockClassType));

        Room result = roomMapper.convertToEntity(mockRoomDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(50, result.getMaxCapacity());
        assertEquals(1, result.getClasses().size());
        assertTrue(result.getClasses().contains(mockFitnessClass));
        assertEquals(1, result.getClassTypes().size());
        assertTrue(result.getClassTypes().contains(mockClassType));
        verify(fitnessClassRepository).findById(10);
        verify(classTypeRepository).findById(20);
    }

    @Test
    void convertToEntity_ThrowsException_WhenFitnessClassNotFound() {
        when(fitnessClassRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roomMapper.convertToEntity(mockRoomDto));
        verify(fitnessClassRepository).findById(10);
    }

    @Test
    void convertToEntity_ThrowsException_WhenClassTypeNotFound() {
        when(fitnessClassRepository.findById(10)).thenReturn(Optional.of(mockFitnessClass));
        when(classTypeRepository.findById(20)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roomMapper.convertToEntity(mockRoomDto));
        verify(fitnessClassRepository).findById(10);
        verify(classTypeRepository).findById(20);
    }

    @Test
    void convertToEntity_ReturnsEmptyCollections_WhenIDsAreEmpty() {
        mockRoomDto.setFitnessClassIds(Collections.emptySet());
        mockRoomDto.setClassTypeIds(Collections.emptySet());

        Room result = roomMapper.convertToEntity(mockRoomDto);

        assertNotNull(result);
        assertTrue(result.getClasses().isEmpty());
        assertTrue(result.getClassTypes().isEmpty());
        verifyNoInteractions(fitnessClassRepository, classTypeRepository);
    }

    @Test
    void convertToEntity_ReturnsEmptyEntity_WhenDtoIsEmpty() {
        RoomDto emptyDto = new RoomDto();
        Room result = roomMapper.convertToEntity(emptyDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(0, result.getMaxCapacity());
        assertTrue(result.getClasses().isEmpty());
        assertTrue(result.getClassTypes().isEmpty());
    }

    @Test
    void convertToDto_Successful() {
        RoomDto result = roomMapper.convertToDto(mockRoom);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(50, result.getMaxCapacity());
        assertEquals(Set.of(10), result.getFitnessClassIds());
        assertEquals(Set.of(20), result.getClassTypeIds());
    }

    @Test
    void convertToDto_ReturnsDtoWithEmptyCollections_WhenEntityHasEmptyCollections() {
        mockRoom.setClasses(Collections.emptySet());
        mockRoom.setClassTypes(Collections.emptySet());

        RoomDto result = roomMapper.convertToDto(mockRoom);

        assertNotNull(result);
        assertTrue(result.getFitnessClassIds().isEmpty());
        assertTrue(result.getClassTypeIds().isEmpty());
    }

    @Test
    void convertToDto_ThrowsException_WhenEntityIsNull() {
        assertThrows(NullPointerException.class, () -> roomMapper.convertToDto(null));
    }

    @Test
    void convertManyToDto_Successful() {
        List<RoomDto> result = roomMapper.convertManyToDto(List.of(mockRoom));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(50, result.get(0).getMaxCapacity());
        assertEquals(Set.of(10), result.get(0).getFitnessClassIds());
        assertEquals(Set.of(20), result.get(0).getClassTypeIds());
    }

    @Test
    void convertManyToDto_ReturnsEmptyList_WhenInputIsEmpty() {
        List<RoomDto> result = roomMapper.convertManyToDto(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
