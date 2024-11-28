package cz.cvut.fit.tjv.fitnessApp.unit.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.FitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.domain.*;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.FitnessClassMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FitnessClassMapperTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ClassTypeRepository classTypeRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private FitnessClassMapper fitnessClassMapper;

    private FitnessClass mockFitnessClass;
    private Instructor mockInstructor;
    private Room mockRoom;
    private ClassType mockClassType;
    private Trainee mockTrainee;
    private FitnessClassDto mockFitnessClassDto;

    @BeforeEach
    void setUp() {
        // Mock FitnessClass
        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(1L);
        mockFitnessClass.setCapacity(5);
        mockFitnessClass.setDate(LocalDate.of(2024, 1, 1));
        mockFitnessClass.setTime(LocalTime.of(10, 0));

        mockInstructor = new Instructor();
        mockInstructor.setId(10L);
        mockFitnessClass.setInstructor(mockInstructor);

        mockRoom = new Room();
        mockRoom.setId(20L);
        mockFitnessClass.setRoom(mockRoom);

        mockClassType = new ClassType();
        mockClassType.setId(30L);
        mockFitnessClass.setClassType(mockClassType);

        mockTrainee = new Trainee();
        mockTrainee.setId(40L);
        mockFitnessClass.setTrainees(List.of(mockTrainee));

        // Mock DTO
        mockFitnessClassDto = new FitnessClassDto();
        mockFitnessClassDto.setId(1L);
        mockFitnessClassDto.setCapacity(5);
        mockFitnessClassDto.setDate(LocalDate.of(2024, 1, 1));
        mockFitnessClassDto.setTime(LocalTime.of(10, 0));
        mockFitnessClassDto.setInstructorId(10L);
        mockFitnessClassDto.setRoomId(20L);
        mockFitnessClassDto.setClassTypeId(30L);
        mockFitnessClassDto.setTraineeIds(List.of(40L));
    }

    @AfterEach
    void tearDown() {
        mockFitnessClass = null;
        mockInstructor = null;
        mockRoom = null;
        mockClassType = null;
        mockTrainee = null;
        mockFitnessClassDto = null;
    }

    @Test
    void convertToEntity_Successful() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.of(mockInstructor));
        when(roomRepository.findById(20L)).thenReturn(Optional.of(mockRoom));
        when(classTypeRepository.findById(30L)).thenReturn(Optional.of(mockClassType));
        when(traineeRepository.findById(40L)).thenReturn(Optional.of(mockTrainee));

        FitnessClass result = fitnessClassMapper.convertToEntity(mockFitnessClassDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(5, result.getCapacity());
        assertEquals(LocalDate.of(2024, 1, 1), result.getDate());
        assertEquals(LocalTime.of(10, 0), result.getTime());
        assertEquals(mockInstructor, result.getInstructor());
        assertEquals(mockRoom, result.getRoom());
        assertEquals(mockClassType, result.getClassType());
        assertEquals(1, result.getTrainees().size());
        assertTrue(result.getTrainees().contains(mockTrainee));
    }

    @Test
    void convertToEntity_ThrowsException_WhenInstructorNotFound() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> fitnessClassMapper.convertToEntity(mockFitnessClassDto));
    }

    @Test
    void convertToEntity_ThrowsException_WhenRoomNotFound() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.of(mockInstructor));
        when(roomRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> fitnessClassMapper.convertToEntity(mockFitnessClassDto));
    }

    @Test
    void convertToEntity_ThrowsException_WhenClassTypeNotFound() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.of(mockInstructor));
        when(roomRepository.findById(20L)).thenReturn(Optional.of(mockRoom));
        when(classTypeRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> fitnessClassMapper.convertToEntity(mockFitnessClassDto));
    }

    @Test
    void convertToEntity_ThrowsException_WhenTraineeNotFound() {
        when(instructorRepository.findById(10L)).thenReturn(Optional.of(mockInstructor));
        when(roomRepository.findById(20L)).thenReturn(Optional.of(mockRoom));
        when(classTypeRepository.findById(30L)).thenReturn(Optional.of(mockClassType));
        when(traineeRepository.findById(40L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> fitnessClassMapper.convertToEntity(mockFitnessClassDto));
    }

    @Test
    void convertToDto_Successful() {
        FitnessClassDto result = fitnessClassMapper.convertToDto(mockFitnessClass);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(5, result.getCapacity());
        assertEquals(LocalDate.of(2024, 1, 1), result.getDate());
        assertEquals(LocalTime.of(10, 0), result.getTime());
        assertEquals(10, result.getInstructorId());
        assertEquals(20, result.getRoomId());
        assertEquals(30, result.getClassTypeId());
        assertEquals(List.of(40L), result.getTraineeIds());
    }

    @Test
    void convertToDto_ReturnsDtoWithEmptyCollections_WhenEntityHasNoTrainees() {
        mockFitnessClass.setTrainees(Collections.emptyList());

        FitnessClassDto result = fitnessClassMapper.convertToDto(mockFitnessClass);

        assertNotNull(result);
        assertTrue(result.getTraineeIds().isEmpty());
    }

    @Test
    void convertManyToDto_Successful() {
        List<FitnessClassDto> result = fitnessClassMapper.convertManyToDto(List.of(mockFitnessClass));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void convertManyToDto_ReturnsEmptyList_WhenInputIsEmpty() {
        List<FitnessClassDto> result = fitnessClassMapper.convertManyToDto(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}