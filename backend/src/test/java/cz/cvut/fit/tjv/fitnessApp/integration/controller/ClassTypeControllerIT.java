package cz.cvut.fit.tjv.fitnessApp.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.classType.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.classType.CreateClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import cz.cvut.fit.tjv.fitnessApp.testUtils.ErrorMatcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

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
class ClassTypeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClassTypeRepository classTypeRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Test
    void create_ShouldPersistClassType() throws Exception {
        // Arrange
        CreateClassTypeDto createDto = new CreateClassTypeDto();
        createDto.setName("Pilates Advanced");

        // Act & Assert
        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Pilates Advanced"));

        // Verify persistence in the database
        List<ClassType> classTypes = classTypeRepository.findByNameContainingIgnoreCase("Pilates Advanced");
        assertEquals(1, classTypes.size());
        assertEquals("Pilates Advanced", classTypes.get(0).getName());
    }

    @Test
    void create_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Arrange
        CreateClassTypeDto invalidDto = new CreateClassTypeDto(); // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.containsErrorMessage("Data integrity violation"));
    }


    @Test
    void createAndUpdateClassType_ShouldReflectChangesInAssociations() throws Exception {
        // Arrange: Create DTO
        CreateClassTypeDto createDto = new CreateClassTypeDto();
        createDto.setName("New ClassType");

        // Act: Create the ClassType
        MvcResult createResult = mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse the created DTO from the response
        ClassTypeDto createdDto = objectMapper.readValue(createResult.getResponse().getContentAsString(), ClassTypeDto.class);

        // Verify creation
        assertNotNull(createdDto.getId());
        assertEquals("New ClassType", createdDto.getName());

        // Arrange: Update the ClassType with associations
        ClassTypeDto updateDto = new ClassTypeDto();
        updateDto.setId(createdDto.getId());
        updateDto.setName("Updated ClassType");
        updateDto.setInstructorIds(List.of(1L, 2L));
        updateDto.setRoomIds(List.of(3L));

        // Act: Update the ClassType
        mockMvc.perform(put("/classtype/" + updateDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        // Verify Update: Retrieve the updated ClassType from the database
        ClassType updatedClassType = classTypeRepository.findById(updateDto.getId())
                .orElseThrow(() -> new AssertionError("Updated ClassType not found in the database"));
        assertEquals("Updated ClassType", updatedClassType.getName());

        // Verify associations
        assertEquals(2, updatedClassType.getInstructors().size());
        assertTrue(updatedClassType.getInstructors().stream().anyMatch(instructor -> instructor.getId() == 1L));
        assertTrue(updatedClassType.getInstructors().stream().anyMatch(instructor -> instructor.getId() == 2L));
        assertEquals(1, updatedClassType.getRooms().size());
        assertEquals(3L, updatedClassType.getRooms().get(0).getId());
    }

    @Test
    void readAllOrByName_ShouldReturnClassTypes() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/classtype"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder("Yoga", "Pilates", "Zumba", "Power Yoga", "Spin")));
    }

    @Test
    void readAllOrByName_ShouldReturnFilteredClassTypes() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/classtype?name=Yoga"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder("Yoga", "Power Yoga")));
    }

    @Test
    void readById_ShouldReturnClassType() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/classtype/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Yoga"));
    }

    @Test
    void readById_ShouldReturnBadRequest_ForInvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/classtype/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ShouldModifyClassTypeAndUpdateAssociations() throws Exception {
        // Arrange
        ClassTypeDto classTypeDto = new ClassTypeDto();
        classTypeDto.setName("Yoga Updated");

        // Act & Assert
        mockMvc.perform(put("/classtype/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classTypeDto)))
                .andExpect(status().isNoContent());

        // Verify update in the database
        ClassType updatedClassType = classTypeRepository.findById(1L).orElseThrow();
        assertEquals("Yoga Updated", updatedClassType.getName());

        // Verify that associated FitnessClasses reference the updated ClassType
        List<FitnessClass> associatedClasses = (List<FitnessClass>) fitnessClassRepository.findAll();
        for (FitnessClass fitnessClass : associatedClasses) {
            if (fitnessClass.getClassType().getId().equals(1L)) {
                assertEquals("Yoga Updated", fitnessClass.getClassType().getName());
            }
        }

        // Verify that associated Rooms reference the updated ClassType
        List<Room> associatedRooms = (List<Room>) roomRepository.findAll();
        for (Room room : associatedRooms) {
            room.getClassTypes().stream()
                    .filter(ct -> ct.getId().equals(1L)) // Filter ClassTypes with id=1L
                    .forEach(ct -> assertEquals("Yoga Updated", ct.getName(),
                            "ClassType with id=1L in room must have name 'Yoga Updated'"));
        }

        // Verify that associated Instructors reference the updated ClassType
        List<Instructor> associatedInstructors = (List<Instructor>) instructorRepository.findAll();
        for (Instructor instructor : associatedInstructors) {
            instructor.getSpecializations().stream()
                    .filter(ct -> ct.getId().equals(1L)) // Filter ClassTypes with id=1L
                    .forEach(ct -> assertEquals("Yoga Updated", ct.getName(),
                            "ClassType with id=1L in instructor specializations must have name 'Yoga Updated'"));
        }
    }

    // TODO: fix the updates
    /*
    @Test
    void update_ShouldReturnBadRequest_ForInvalidData() throws Exception {
        // Arrange
        ClassTypeDto invalidDto = new ClassTypeDto();

        // Act & Assert
        mockMvc.perform(put("/classtype/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
     */

    @Test
    void deleteById_ShouldRemoveClassTypeAndUpdateAssociations() throws Exception {
        // Act
        mockMvc.perform(delete("/classtype/1"))
                .andExpect(status().isNoContent());

        // Verify the ClassType is removed from the database
        assertFalse(classTypeRepository.existsById(1L));

        // Verify that all FitnessClasses with this ClassType are deleted
        List<FitnessClass> remainingClasses = (List<FitnessClass>) fitnessClassRepository.findAll();
        assertTrue(remainingClasses.stream().noneMatch(fc -> fc.getClassType().getId().equals(1L)));

        // Verify that the ClassType is removed from all associated Rooms
        List<Room> rooms = (List<Room>) roomRepository.findAll();
        for (Room room : rooms) {
            assertTrue(room.getClassTypes().stream().noneMatch(ct -> ct.getId().equals(1L)));
        }

        // Verify that the ClassType is removed from all associated Instructors
        List<Instructor> instructors = (List<Instructor>) instructorRepository.findAll();
        for (Instructor instructor : instructors) {
            assertTrue(instructor.getSpecializations().stream().noneMatch(ct -> ct.getId().equals(1L)));
        }
    }

    @Test
    void deleteById_ShouldReturnBadRequest_ForInvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/classtype/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getInstructorsByClassType_ShouldReturnInstructors() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/classtype/1/instructors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRoomsByClassType_ShouldReturnRooms() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/classtype/1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getFitnessClassesByClassType_ShouldReturnFitnessClasses() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/classtype/1/fitness-classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
