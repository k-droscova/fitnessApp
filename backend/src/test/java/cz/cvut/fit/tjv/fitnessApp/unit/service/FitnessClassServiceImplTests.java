package cz.cvut.fit.tjv.fitnessApp.unit.service;

import cz.cvut.fit.tjv.fitnessApp.domain.*;
import cz.cvut.fit.tjv.fitnessApp.repository.*;
import cz.cvut.fit.tjv.fitnessApp.service.FitnessClassServiceImpl;
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
class FitnessClassServiceImplTest {

    @Mock
    private FitnessClassRepository fitnessClassRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ClassTypeRepository classTypeRepository;

    @InjectMocks
    private FitnessClassServiceImpl fitnessClassService;

    private FitnessClass mockFitnessClass;
    private Room mockRoom;
    private Instructor mockInstructor;

    @BeforeEach
    void setUp() {
        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(1L);
        mockFitnessClass.setCapacity(20);
        mockFitnessClass.setDate(LocalDate.now().plusDays(1));
        mockFitnessClass.setTime(LocalTime.now().plusHours(2));

        mockRoom = new Room();
        mockRoom.setId(1L);
        mockRoom.setMaxCapacity(30);
        mockFitnessClass.setRoom(mockRoom);

        mockInstructor = new Instructor();
        mockInstructor.setId(1L);
        mockFitnessClass.setInstructor(mockInstructor);
    }

    @AfterEach
    void tearDown() {
        mockFitnessClass = null;
        mockRoom = null;
        mockInstructor = null;
    }
    
    @Test
    void create_Successful() {
        FitnessClass fitnessClass = new FitnessClass();
        when(fitnessClassRepository.save(any(FitnessClass.class))).thenReturn(mockFitnessClass);

        FitnessClass result = fitnessClassService.create(fitnessClass);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(20, result.getCapacity());
        verify(fitnessClassRepository).save(fitnessClass);
    }

    @Test
    void create_ThrowsException_WhenIdIsPassed() {
        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.create(mockFitnessClass));
        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void readById_ReturnsEntity_WhenFound() {
        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.of(mockFitnessClass));

        Optional<FitnessClass> result = fitnessClassService.readById(1L);

        assertTrue(result.isPresent());
        assertEquals(20, result.get().getCapacity());
    }

    @Test
    void readById_ReturnsEmpty_WhenNotFound() {
        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<FitnessClass> result = fitnessClassService.readById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void readAll_ReturnsEntities() {
        when(fitnessClassRepository.findAll()).thenReturn(List.of(mockFitnessClass));

        List<FitnessClass> result = fitnessClassService.readAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void readAll_ReturnsEmpty_WhenNoEntities() {
        when(fitnessClassRepository.findAll()).thenReturn(List.of());

        List<FitnessClass> result = fitnessClassService.readAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void update_Successful() {
        when(fitnessClassRepository.existsById(1L)).thenReturn(true);
        when(fitnessClassRepository.save(mockFitnessClass)).thenReturn(mockFitnessClass);

        fitnessClassService.update(1L, mockFitnessClass);

        verify(fitnessClassRepository).save(mockFitnessClass);
    }

    @Test
    void update_ThrowsException_WhenEntityDoesNotExist() {
        when(fitnessClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.update(1L, mockFitnessClass));
        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void deleteById_Successful() {
        when(fitnessClassRepository.existsById(1L)).thenReturn(true);

        fitnessClassService.deleteById(1L);

        verify(fitnessClassRepository).deleteById(1L);
    }

    @Test
    void deleteById_ThrowsException_WhenEntityDoesNotExist() {
        when(fitnessClassRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.deleteById(1L));
        verify(fitnessClassRepository, never()).deleteById(any());
    }

    @Test
    void scheduleClass_ShouldScheduleSuccessfully() {
        FitnessClass fitnessClass = mockFitnessClass;
        fitnessClass.setId(null);
        when(fitnessClassRepository.findFitnessClassesByDateAndRoom_Id(any(), any())).thenReturn(List.of());
        when(fitnessClassRepository.findFitnessClassesByDate(any())).thenReturn(List.of());
        when(fitnessClassRepository.save(any(FitnessClass.class))).thenReturn(mockFitnessClass);

        fitnessClassService.scheduleClass(fitnessClass);

        verify(fitnessClassRepository).save(fitnessClass);
    }

    @Test
    void scheduleClass_ShouldThrowException_WhenRoomUnavailable() {
        when(fitnessClassRepository.findFitnessClassesByDateAndRoom_Id(any(), any()))
                .thenReturn(List.of(mockFitnessClass));

        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.scheduleClass(mockFitnessClass));

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void scheduleClass_ShouldThrowException_WhenInstructorUnavailable() {
        when(fitnessClassRepository.findFitnessClassesByDate(any())).thenReturn(List.of(mockFitnessClass));

        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.scheduleClass(mockFitnessClass));

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void scheduleClass_ShouldThrowException_WhenDateInPast() {
        mockFitnessClass.setDate(LocalDate.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> fitnessClassService.scheduleClass(mockFitnessClass));

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void scheduleClass_ShouldThrowException_WhenClassCapacityExceedsRoomCapacity() {
        mockFitnessClass.setCapacity(35);

        assertThrows(IllegalArgumentException.class,
                () -> fitnessClassService.scheduleClass(mockFitnessClass),
                "Expected IllegalArgumentException when class capacity exceeds room's maximum capacity.");

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void scheduleClass_ShouldThrowException_WhenClassCapacityBelowZero() {
        mockFitnessClass.setCapacity(-5);
        assertThrows(IllegalArgumentException.class,
                () -> fitnessClassService.scheduleClass(mockFitnessClass),
                "Expected IllegalArgumentException when class capacity is below zero.");

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void validateAndUpdate_ShouldUpdateSuccessfully() {
        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.of(mockFitnessClass));
        when(fitnessClassRepository.findFitnessClassesByDateAndRoom_Id(any(), any())).thenReturn(List.of());
        when(fitnessClassRepository.findFitnessClassesByDate(any())).thenReturn(List.of());

        when(roomRepository.findById(any())).thenReturn(Optional.of(mockRoom));
        when(instructorRepository.findById(any())).thenReturn(Optional.of(mockInstructor));

        mockFitnessClass.setCapacity(25);
        fitnessClassService.validateAndUpdate(1L, mockFitnessClass);

        verify(fitnessClassRepository).save(mockFitnessClass);
    }


    @Test
    void validateAndUpdate_ShouldThrowException_WhenRoomConflict() {
        FitnessClass conflict = new FitnessClass();
        conflict.setId(100L);
        conflict.setTime(mockFitnessClass.getTime());
        conflict.setRoom(mockRoom);
        conflict.setDate(mockFitnessClass.getDate());

        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.of(mockFitnessClass));
        when(fitnessClassRepository.findFitnessClassesByDateAndRoom_Id(any(), any()))
                .thenReturn(List.of(conflict));

        assertThrows(IllegalArgumentException.class,
                () -> fitnessClassService.validateAndUpdate(1L, mockFitnessClass));

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void validateAndUpdate_ShouldThrowException_WhenInstructorConflict() {
        FitnessClass existingClass = new FitnessClass();
        existingClass.setId(2L); // Different ID
        existingClass.setDate(mockFitnessClass.getDate());
        existingClass.setTime(mockFitnessClass.getTime());
        existingClass.setInstructor(mockInstructor); // Same instructor

        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.of(mockFitnessClass));
        when(fitnessClassRepository.findFitnessClassesByDate(mockFitnessClass.getDate())).thenReturn(List.of(existingClass));

        assertThrows(IllegalArgumentException.class,
                () -> fitnessClassService.validateAndUpdate(1L, mockFitnessClass));

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void validateAndUpdate_ShouldThrowException_WhenDateInPast() {
        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.of(mockFitnessClass));
        mockFitnessClass.setDate(LocalDate.of(2023, 12, 1));

        assertThrows(IllegalArgumentException.class,
                () -> fitnessClassService.validateAndUpdate(1L, mockFitnessClass));

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void addTraineeToClass_ShouldAddTraineeSuccessfully() {
        Trainee mockTrainee = new Trainee();
        mockTrainee.setId(1L);

        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.of(mockFitnessClass));
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(mockTrainee));

        fitnessClassService.addTraineeToClass(1L, 1L);

        assertTrue(mockFitnessClass.getTrainees().contains(mockTrainee));
        verify(fitnessClassRepository).save(mockFitnessClass);
    }

    @Test
    void addTraineeToClass_ShouldThrowException_WhenCapacityFull() {
        Trainee mockTrainee = new Trainee();
        mockTrainee.setId(1L);
        mockFitnessClass.setCapacity(1);
        mockFitnessClass.setTrainees(List.of(mockTrainee));

        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.of(mockFitnessClass));

        assertThrows(IllegalArgumentException.class,
                () -> fitnessClassService.addTraineeToClass(1L, 2L));

        verify(fitnessClassRepository, never()).save(any());
    }

    @Test
    void addTraineeToClass_ShouldThrowException_WhenTraineeAlreadyEnrolled() {
        Trainee mockTrainee = new Trainee();
        mockTrainee.setId(1L);
        mockFitnessClass.getTrainees().add(mockTrainee);

        when(fitnessClassRepository.findById(1L)).thenReturn(Optional.of(mockFitnessClass));
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(mockTrainee));

        assertThrows(IllegalArgumentException.class,
                () -> fitnessClassService.addTraineeToClass(1L, 1L));

        verify(fitnessClassRepository, never()).save(any());
    }
}
