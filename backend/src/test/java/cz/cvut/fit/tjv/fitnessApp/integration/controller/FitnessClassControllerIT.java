package cz.cvut.fit.tjv.fitnessApp.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.fitnessClass.CreateFitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.fitnessClass.FitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.domain.*;
import cz.cvut.fit.tjv.fitnessApp.repository.*;
import cz.cvut.fit.tjv.fitnessApp.testUtils.ErrorMatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use test profile with preloaded data
@Transactional // Ensure each test runs in a transaction and rolls back afterward
class FitnessClassControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private ClassTypeRepository classTypeRepository;

    @Test
    void create_ShouldPersistFitnessClassAndVerifyAssociations() throws Exception {
        // Arrange
        CreateFitnessClassDto createDto = new CreateFitnessClassDto();
        createDto.setDate(LocalDate.now().plusDays(4));
        createDto.setTime(LocalTime.of(10, 0));
        createDto.setCapacity(15);
        createDto.setInstructorId(1L); // Preloaded instructor
        createDto.setRoomId(2L); // Preloaded room
        createDto.setClassTypeId(1L); // Preloaded class type

        // Act
        MvcResult result = mockMvc.perform(post("/fitness-class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.date").value(createDto.getDate().toString()))
                .andExpect(jsonPath("$.time").value("10:00:00"))
                .andExpect(jsonPath("$.capacity").value(15))
                .andReturn();

        // Extract the ID from the response
        String responseContent = result.getResponse().getContentAsString();
        FitnessClassDto createdFitnessClass = objectMapper.readValue(responseContent, FitnessClassDto.class);
        Long createdId = createdFitnessClass.getId();
        assertNotNull(createdId);

        // Verify the persisted data
        FitnessClass persistedClass = fitnessClassRepository.findById(createdId)
                .orElseThrow(() -> new AssertionError("FitnessClass not found in the database"));
        assertEquals(createDto.getDate(), persistedClass.getDate());
        assertEquals(LocalTime.of(10, 0), persistedClass.getTime());
        assertEquals(15, persistedClass.getCapacity());
        assertEquals(1L, persistedClass.getInstructor().getId());
        assertEquals(2L, persistedClass.getRoom().getId());
        assertEquals(1L, persistedClass.getClassType().getId());

        // Fetch and verify associations with the new FitnessClass
        Instructor associatedInstructor = instructorRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Instructor not found in the database"));
        assertTrue(associatedInstructor.getClasses().stream()
                .anyMatch(fc -> Objects.equals(fc.getId(), createdId)), "Instructor should be associated with the new FitnessClass");

        Room associatedRoom = roomRepository.findById(2L)
                .orElseThrow(() -> new AssertionError("Room not found in the database"));
        assertTrue(associatedRoom.getClasses().stream()
                .anyMatch(fc -> Objects.equals(fc.getId(), createdId)), "Room should be associated with the new FitnessClass");

        ClassType associatedClassType = classTypeRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("ClassType not found in the database"));
        assertTrue(associatedClassType.getClasses().stream()
                .anyMatch(fc -> Objects.equals(fc.getId(), createdId)), "ClassType should be associated with the new FitnessClass");
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Arrange
        CreateFitnessClassDto invalidDto = new CreateFitnessClassDto(); // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/fitness-class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldModifyFitnessClassAndVerifyAssociations() throws Exception {
        // Arrange
        LocalDate date = LocalDate.now().plusDays(7);
        FitnessClassDto updateDto = new FitnessClassDto();
        updateDto.setDate(date);
        updateDto.setTime(LocalTime.of(12, 30));
        updateDto.setCapacity(5);
        updateDto.setInstructorId(2L); // Preloaded instructor
        updateDto.setRoomId(3L); // Preloaded room
        updateDto.setClassTypeId(2L); // Preloaded class type

        // Act
        mockMvc.perform(put("/fitness-class/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        // Verify update
        FitnessClass updatedClass = fitnessClassRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("FitnessClass not found in the database"));
        assertEquals(date, updatedClass.getDate());
        assertEquals(LocalTime.of(12, 30), updatedClass.getTime());
        assertEquals(5, updatedClass.getCapacity());
        assertEquals(2L, updatedClass.getInstructor().getId());
        assertEquals(3L, updatedClass.getRoom().getId());
        assertEquals(2L, updatedClass.getClassType().getId());

        // Verify associations
        // Verify Room association
        Room associatedRoom = roomRepository.findById(3L)
                .orElseThrow(() -> new AssertionError("Room not found in the database"));
        assertTrue(associatedRoom.getClasses().stream()
                        .anyMatch(fc -> Objects.equals(fc.getId(), updatedClass.getId())),
                "Room should be associated with the updated FitnessClass");

        // Verify Instructor association
        Instructor associatedInstructor = instructorRepository.findById(2L)
                .orElseThrow(() -> new AssertionError("Instructor not found in the database"));
        assertTrue(associatedInstructor.getClasses().stream()
                        .anyMatch(fc -> Objects.equals(fc.getId(), updatedClass.getId())),
                "Instructor should be associated with the updated FitnessClass");

        // Verify ClassType association
        ClassType associatedClassType = classTypeRepository.findById(2L)
                .orElseThrow(() -> new AssertionError("ClassType not found in the database"));
        assertTrue(associatedClassType.getClasses().stream()
                        .anyMatch(fc -> Objects.equals(fc.getId(), updatedClass.getId())),
                "ClassType should be associated with the updated FitnessClass");

        // Verify Trainee association (if applicable)
        if (!updatedClass.getTrainees().isEmpty()) {
            for (Trainee attendee : updatedClass.getTrainees()) {
                Trainee associatedTrainee = traineeRepository.findById(attendee.getId())
                        .orElseThrow(() -> new AssertionError("Trainee not found in the database"));
                assertTrue(associatedTrainee.getClasses().stream()
                                .anyMatch(fc -> Objects.equals(fc.getId(), updatedClass.getId())),
                        "Trainee should be associated with the updated FitnessClass");
            }
        }
    }

    @Test
    void readAllWithFilters_ShouldReturnFilteredFitnessClasses() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fitness-class?date=2024-12-01&startTime=10:00:00&endTime=18:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)); // Assuming 3 classes in the range
    }

    @Test
    void readById_ShouldReturnFitnessClass() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fitness-class/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2024-12-01"));
    }

    @Test
    void deleteById_ShouldRemoveFitnessClassAndVerifyAssociations() throws Exception {
        // Act
        mockMvc.perform(delete("/fitness-class/1"))
                .andExpect(status().isNoContent());

        // Verify the FitnessClass is removed
        assertFalse(fitnessClassRepository.existsById(1L), "FitnessClass with ID 1 should no longer exist");

        // Verify Room associations
        List<Room> rooms = (List<Room>) roomRepository.findAll();
        for (Room room : rooms) {
            assertTrue(room.getClasses().stream()
                            .noneMatch(fitnessClass -> Objects.equals(fitnessClass.getId(), 1L)),
                    "Room should not be associated with the deleted FitnessClass");
        }

        // Verify Instructor associations
        List<Instructor> instructors = (List<Instructor>) instructorRepository.findAll();
        for (Instructor instructor : instructors) {
            assertTrue(instructor.getClasses().stream()
                            .noneMatch(fitnessClass -> Objects.equals(fitnessClass.getId(), 1L)),
                    "Instructor should not be associated with the deleted FitnessClass");
        }

        // Verify Trainee associations
        List<Trainee> trainees = (List<Trainee>) traineeRepository.findAll();
        for (Trainee trainee : trainees) {
            assertTrue(trainee.getClasses().stream()
                            .noneMatch(fitnessClass -> Objects.equals(fitnessClass.getId(), 1L)),
                    "Trainee should not be associated with the deleted FitnessClass");
        }
    }

    @Test
    void addTraineeToClass_ShouldUpdateAssociations() throws Exception {
        // Arrange: Dynamically create a future-dated fitness class
        LocalDate futureDate = LocalDate.now().plusMonths(1); // Tomorrow
        LocalTime futureTime = LocalTime.of(10, 0); // 10:00 AM
        FitnessClass futureClass = new FitnessClass();
        futureClass.setDate(futureDate);
        futureClass.setTime(futureTime);
        futureClass.setCapacity(5);
        futureClass.setInstructor(instructorRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Instructor not found")));
        futureClass.setRoom(roomRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Room not found")));
        futureClass.setClassType(classTypeRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("ClassType not found")));
        FitnessClass savedClass = fitnessClassRepository.save(futureClass);

        // Act: Add a trainee to the future-dated fitness class
        mockMvc.perform(post("/fitness-class/" + savedClass.getId() + "/add-trainee/1"))
                .andExpect(status().isNoContent());

        // Verify the association in the FitnessClass
        FitnessClass updatedClass = fitnessClassRepository.findById(savedClass.getId())
                .orElseThrow(() -> new AssertionError("FitnessClass not found in the database"));
        assertTrue(updatedClass.getTrainees().stream()
                        .anyMatch(trainee -> trainee.getId() == 1L),
                "FitnessClass should include the Trainee with ID 1");

        // Verify the association in the Trainee
        Trainee trainee = traineeRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Trainee not found in the database"));
        assertTrue(trainee.getClasses().stream()
                        .anyMatch(fClass -> Objects.equals(fClass.getId(), savedClass.getId())),
                "Trainee should be associated with the FitnessClass with ID " + savedClass.getId());
    }

    @Test
    void getTraineesByFitnessClass_ShouldReturnTrainees() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fitness-class/1/trainees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)); // Assuming 3 attendees for fitness class 1
    }

    @Test
    void removeTraineeFromClass_ShouldRemoveTraineeFromFutureClass() throws Exception {
        // Arrange: Create a FitnessClass in the future
        LocalDate futureDate = LocalDate.now().plusDays(10); // 10 days from now
        LocalTime futureTime = LocalTime.of(10, 0); // 10:00 AM
        FitnessClass futureClass = new FitnessClass();
        futureClass.setDate(futureDate);
        futureClass.setTime(futureTime);
        futureClass.setCapacity(10);
        futureClass.setInstructor(instructorRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Instructor not found")));
        futureClass.setRoom(roomRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Room not found")));
        futureClass.setClassType(classTypeRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("ClassType not found")));
        FitnessClass savedClass = fitnessClassRepository.save(futureClass);

        // Arrange: Add a trainee to the future FitnessClass
        mockMvc.perform(post("/fitness-class/" + savedClass.getId() + "/add-trainee/1"))
                .andExpect(status().isNoContent());

        // Act: Remove the trainee from the FitnessClass
        mockMvc.perform(post("/fitness-class/" + savedClass.getId() + "/remove-trainee/1"))
                .andExpect(status().isNoContent());

        // Assert: Verify the trainee is removed from the FitnessClass
        FitnessClass updatedClass = fitnessClassRepository.findById(savedClass.getId())
                .orElseThrow(() -> new AssertionError("FitnessClass not found in the database"));
        assertTrue(updatedClass.getTrainees().stream()
                .noneMatch(trainee -> trainee.getId().equals(1L)), "Trainee should no longer be enrolled in the FitnessClass");

        // Assert: Verify the FitnessClass is removed from the trainee's list of classes
        Trainee trainee = traineeRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Trainee not found in the database"));
        assertTrue(trainee.getClasses().stream()
                        .noneMatch(fitnessClass -> fitnessClass.getId().equals(savedClass.getId())),
                "FitnessClass should no longer be associated with the trainee");
    }
}
