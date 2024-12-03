package cz.cvut.fit.tjv.fitnessApp.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoomControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ClassTypeRepository classTypeRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Test
    void create_ShouldPersistRoomAndAssociations() throws Exception {
        // Arrange
        RoomDto roomDto = new RoomDto();
        roomDto.setMaxCapacity(25);
        roomDto.setClassTypeIds(List.of(1L, 2L)); // Preloaded ClassType IDs
        roomDto.setFitnessClassIds(List.of(3L)); // Preloaded FitnessClass ID

        // Act
        MvcResult result = mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        // Verify Room persistence
        RoomDto createdRoomDto = objectMapper.readValue(result.getResponse().getContentAsString(), RoomDto.class);
        assertNotNull(createdRoomDto.getId());
        assertEquals(25, createdRoomDto.getMaxCapacity());

        // Verify Room associations
        Room createdRoom = roomRepository.findById(createdRoomDto.getId())
                .orElseThrow(() -> new AssertionError("Room not found"));
        assertEquals(2, createdRoom.getClassTypes().size());
        assertTrue(createdRoom.getClassTypes().stream().anyMatch(ct -> ct.getId() == 1L));
        assertTrue(createdRoom.getClassTypes().stream().anyMatch(ct -> ct.getId() == 2L));
        assertEquals(1, createdRoom.getClasses().size());
        assertTrue(createdRoom.getClasses().stream().anyMatch(fc -> fc.getId() == 3L));

        // Verify inverse associations for ClassType
        for (Long classTypeId : roomDto.getClassTypeIds()) {
            ClassType classType = classTypeRepository.findById(classTypeId)
                    .orElseThrow(() -> new AssertionError("ClassType not found: " + classTypeId));
            assertTrue(classType.getRooms().stream()
                            .anyMatch(room -> room.getId().equals(createdRoom.getId())),
                    "ClassType should be associated with the created Room");
        }

        // Verify inverse association for FitnessClass
        for (Long fitnessClassId : roomDto.getFitnessClassIds()) {
            FitnessClass fitnessClass = fitnessClassRepository.findById(fitnessClassId)
                    .orElseThrow(() -> new AssertionError("FitnessClass not found: " + fitnessClassId));
            assertEquals(createdRoom.getId(), fitnessClass.getRoom().getId(),
                    "FitnessClass should be associated with the created Room");
        }
    }

    @Test
    void update_ShouldModifyRoomAndAssociations() throws Exception {
        // Arrange
        RoomDto updateDto = new RoomDto();
        updateDto.setMaxCapacity(30);
        updateDto.setClassTypeIds(List.of(3L)); // Updated ClassType ID
        updateDto.setFitnessClassIds(List.of(4L)); // Updated FitnessClass ID

        // Act
        mockMvc.perform(put("/room/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        // Verify Room update
        Room updatedRoom = roomRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Room not found"));
        assertEquals(30, updatedRoom.getMaxCapacity());

        // Verify updated Room associations
        assertEquals(1, updatedRoom.getClassTypes().size());
        assertTrue(updatedRoom.getClassTypes().stream().anyMatch(ct -> ct.getId() == 3L));
        assertEquals(1, updatedRoom.getClasses().size());
        assertTrue(updatedRoom.getClasses().stream().anyMatch(fc -> fc.getId() == 4L));

        // Verify inverse ClassType association
        ClassType updatedClassType = classTypeRepository.findById(3L)
                .orElseThrow(() -> new AssertionError("ClassType not found"));
        assertTrue(updatedClassType.getRooms().stream()
                        .anyMatch(room -> room.getId() == updatedRoom.getId()),
                "ClassType should be associated with the updated Room");

        // Verify inverse FitnessClass association
        FitnessClass updatedFitnessClass = fitnessClassRepository.findById(4L)
                .orElseThrow(() -> new AssertionError("FitnessClass not found"));
        assertEquals(updatedRoom.getId(), updatedFitnessClass.getRoom().getId(),
                "FitnessClass should reference the updated Room");
    }

    @Test
    void delete_ShouldRemoveRoomAndClearAssociations() throws Exception {
        // Act
        mockMvc.perform(delete("/room/1"))
                .andExpect(status().isNoContent());

        // Verify Room deletion
        assertFalse(roomRepository.existsById(1L));

        // Verify ClassType associations are cleared
        List<ClassType> allClassTypes = (List<ClassType>) classTypeRepository.findAll();
        assertTrue(allClassTypes.stream().noneMatch(classType ->
                        classType.getRooms().stream().anyMatch(room -> room.getId() == 1L)),
                "No ClassType should have a reference to Room with ID 1L");

        // Verify FitnessClasses scheduled in Room 1 are deleted
        assertFalse(fitnessClassRepository.existsById(1L)); // Yoga class in Room 1
        assertFalse(fitnessClassRepository.existsById(2L)); // Pilates class in Room 1

        // Verify other FitnessClasses are unaffected
        assertTrue(fitnessClassRepository.existsById(3L)); // Zumba class in Room 2
        assertTrue(fitnessClassRepository.existsById(4L)); // Power Yoga in Room 4
        assertTrue(fitnessClassRepository.existsById(5L)); // Spin class in Room 5
    }

    @Test
    void readAll_ShouldReturnAllRooms() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/room"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5)); // Assuming 5 rooms are preloaded in the test data
    }

    @Test
    void readById_ShouldReturnRoom() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/room/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.maxCapacity").value(20)); // Assuming max capacity is 20 for Room with ID 1
    }

    @Test
    void readById_ShouldReturnNotFound_ForInvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/room/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAvailableRooms_ShouldReturnAvailableRooms() throws Exception {
        // Act
        mockMvc.perform(get("/room/available")
                        .param("classTypeId", "1")
                        .param("date", "2024-12-05")
                        .param("time", "10:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3)); // Assuming 2 rooms are available for ClassType ID 1 at the given time
    }
}
