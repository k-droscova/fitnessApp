package cz.cvut.fit.tjv.fitnessApp.unit.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.ClassTypeMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassTypeMapperTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @InjectMocks
    private ClassTypeMapper classTypeMapper;

    private ClassType mockClassType;
    private Instructor mockInstructor;
    private Room mockRoom;
    private FitnessClass mockFitnessClass;
    private ClassTypeDto mockClassTypeDto;

    @BeforeEach
    void setUp() {
        // Set up mock class type
        mockClassType = new ClassType();
        mockClassType.setId(1L);
        mockClassType.setName("Yoga");
        mockInstructor = new Instructor();
        mockInstructor.setId(10L);
        mockClassType.setInstructors(List.of(mockInstructor));
        mockRoom = new Room();
        mockRoom.setId(20L);
        mockClassType.setRooms(List.of(mockRoom));
        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(30L);
        mockClassType.setClasses(List.of(mockFitnessClass));

        // Set up mock dto
        mockClassTypeDto = new ClassTypeDto();
        mockClassTypeDto.setId(1L);
        mockClassTypeDto.setName("Yoga");
        mockClassTypeDto.setInstructorIds(List.of(10L));
        mockClassTypeDto.setRoomIds(List.of(20L));
        mockClassTypeDto.setFitnessClassIds(List.of(30L));
    }

    @AfterEach
    void tearDown() {
        mockInstructor = null;
        mockRoom = null;
        mockFitnessClass = null;
        mockClassType = null;
        mockClassTypeDto = null;
    }

    @Test
    void convertToEntity_Successful() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.of(mockInstructor));
        when(roomRepository.findById(20L)).thenReturn(Optional.of(mockRoom));
        when(fitnessClassRepository.findById(30L)).thenReturn(Optional.of(mockFitnessClass));

        // Perform mapping
        ClassType result = classTypeMapper.convertToEntity(mockClassTypeDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Yoga", result.getName());
        assertEquals(1, result.getInstructors().size());
        assertEquals(1, result.getRooms().size());
        assertEquals(1, result.getClasses().size());
        verify(instructorRepository).findById(10L);
        verify(roomRepository).findById(20L);
        verify(fitnessClassRepository).findById(30L);
    }

    @Test
    void convertToEntity_ThrowsException_WhenInstructorNotFound() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> classTypeMapper.convertToEntity(mockClassTypeDto));
        verify(instructorRepository).findById(10L);
    }

    @Test
    void convertToEntity_ThrowsException_WhenRoomNotFound() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.of(mockInstructor));
        when(roomRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> classTypeMapper.convertToEntity(mockClassTypeDto));
        verify(instructorRepository).findById(10L);
        verify(roomRepository).findById(20L);
    }

    @Test
    void convertToEntity_ThrowsException_WhenFitnessClassNotFound() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.of(mockInstructor));
        when(roomRepository.findById(20L)).thenReturn(Optional.of(mockRoom));
        when(fitnessClassRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> classTypeMapper.convertToEntity(mockClassTypeDto));
        verify(instructorRepository).findById(10L);
        verify(roomRepository).findById(20L);
        verify(fitnessClassRepository).findById(30L);
    }

    @Test
    void convertToEntity_ReturnsEmptyCollections_WhenIDsAreEmpty() {
        mockClassTypeDto.setInstructorIds(new ArrayList<>());
        mockClassTypeDto.setRoomIds(new ArrayList<>());
        mockClassTypeDto.setFitnessClassIds(new ArrayList<>());

        ClassType result = classTypeMapper.convertToEntity(mockClassTypeDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Yoga", result.getName());
        assertTrue(result.getInstructors().isEmpty());
        assertTrue(result.getRooms().isEmpty());
        assertTrue(result.getClasses().isEmpty());

        verifyNoInteractions(instructorRepository, roomRepository, fitnessClassRepository);
    }

    @Test
    void convertToEntity_ReturnsEmptyEntity_WhenDtoIsEmpty() {
        ClassTypeDto emptyDto = new ClassTypeDto();
        ClassType result = classTypeMapper.convertToEntity(emptyDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertTrue(result.getInstructors().isEmpty());
        assertTrue(result.getRooms().isEmpty());
        assertTrue(result.getClasses().isEmpty());
    }

    @Test
    void convertToDto_Successful() {
        ClassTypeDto result = classTypeMapper.convertToDto(mockClassType);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Yoga", result.getName());
        assertEquals(List.of(10L), result.getInstructorIds());
        assertEquals(List.of(20L), result.getRoomIds());
        assertEquals(List.of(30L), result.getFitnessClassIds());
    }

    @Test
    void convertToDto_ReturnsDtoWithEmptyCollections_WhenEntityHasEmptyCollections() {
        mockClassType.setInstructors(Collections.emptyList());
        mockClassType.setRooms(Collections.emptyList());
        mockClassType.setClasses(Collections.emptyList());

        ClassTypeDto result = classTypeMapper.convertToDto(mockClassType);

        assertNotNull(result);
        assertNotNull(result.getInstructorIds());
        assertTrue(result.getInstructorIds().isEmpty());
        assertNotNull(result.getRoomIds());
        assertTrue(result.getRoomIds().isEmpty());
        assertNotNull(result.getFitnessClassIds());
        assertTrue(result.getFitnessClassIds().isEmpty());
    }

    @Test
    void convertManyToDto_Successful() {
        List<ClassTypeDto> result = classTypeMapper.convertManyToDto(List.of(mockClassType));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Yoga", result.get(0).getName());
        assertEquals(List.of(10L), result.get(0).getInstructorIds());
        assertEquals(List.of(20L), result.get(0).getRoomIds());
        assertEquals(List.of(30L), result.get(0).getFitnessClassIds());
    }

    @Test
    void convertManyToDto_ReturnsEmptyList_WhenInputIsEmpty() {
        List<ClassTypeDto> result = classTypeMapper.convertManyToDto(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}