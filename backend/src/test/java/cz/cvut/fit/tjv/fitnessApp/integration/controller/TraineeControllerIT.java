package cz.cvut.fit.tjv.fitnessApp.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.trainee.CreateTraineeDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.trainee.TraineeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
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
@Transactional
@ActiveProfiles("test")
public class TraineeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Test
    void create_ShouldPersistTraineeAndAssociations() throws Exception {
        // Arrange
        CreateTraineeDto createTraineeDto = new CreateTraineeDto();
        createTraineeDto.setEmail("newtrainee@example.com");
        createTraineeDto.setName("New");
        createTraineeDto.setSurname("Trainee");
        createTraineeDto.setFitnessClassIds(List.of(1L, 2L)); // Preloaded FitnessClass IDs

        // Act
        MvcResult result = mockMvc.perform(post("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTraineeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        // Verify Trainee persistence
        TraineeDto createdTrainee = objectMapper.readValue(result.getResponse().getContentAsString(), TraineeDto.class);
        assertNotNull(createdTrainee.getId());
        assertEquals("newtrainee@example.com", createdTrainee.getEmail());
        assertEquals("New", createdTrainee.getName());
        assertEquals("Trainee", createdTrainee.getSurname());

        // Verify associations
        Trainee trainee = traineeRepository.findById(createdTrainee.getId())
                .orElseThrow(() -> new AssertionError("Trainee not found"));
        assertEquals(2, trainee.getClasses().size());
        assertTrue(trainee.getClasses().stream().anyMatch(fc -> fc.getId() == 1L));
        assertTrue(trainee.getClasses().stream().anyMatch(fc -> fc.getId() == 2L));

        // Verify FitnessClass associations
        FitnessClass fitnessClass1 = fitnessClassRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("FitnessClass 1 not found"));
        assertTrue(fitnessClass1.getTrainees().stream().anyMatch(t -> t.getId().equals(trainee.getId())));

        FitnessClass fitnessClass2 = fitnessClassRepository.findById(2L)
                .orElseThrow(() -> new AssertionError("FitnessClass 2 not found"));
        assertTrue(fitnessClass2.getTrainees().stream().anyMatch(t -> t.getId().equals(trainee.getId())));
    }

    @Test
    void update_ShouldModifyTraineeAndAssociations() throws Exception {
        // Arrange
        TraineeDto updateDto = new TraineeDto();
        updateDto.setEmail("alice.updated@example.com");
        updateDto.setName("Alice Updated");
        updateDto.setSurname("Doe Updated");
        updateDto.setFitnessClassIds(List.of(3L)); // Updated FitnessClass ID

        // Act
        mockMvc.perform(put("/trainee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        // Verify Trainee update
        Trainee updatedTrainee = traineeRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Trainee not found"));
        assertEquals("alice.updated@example.com", updatedTrainee.getEmail());
        assertEquals("Alice Updated", updatedTrainee.getName());
        assertEquals("Doe Updated", updatedTrainee.getSurname());

        // Verify updated FitnessClass associations
        assertEquals(1, updatedTrainee.getClasses().size());
        assertTrue(updatedTrainee.getClasses().stream().anyMatch(fc -> fc.getId() == 3L));

        // Verify reverse associations for new FitnessClass
        FitnessClass fitnessClass3 = fitnessClassRepository.findById(3L)
                .orElseThrow(() -> new AssertionError("FitnessClass 3 not found"));
        assertTrue(fitnessClass3.getTrainees().stream().anyMatch(t -> t.getId() == 1L));

        // Verify reverse associations for old FitnessClasses
        FitnessClass fitnessClass1 = fitnessClassRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("FitnessClass 1 not found"));
        assertTrue(fitnessClass1.getTrainees().stream().noneMatch(t -> t.getId() == 1L));

        FitnessClass fitnessClass4 = fitnessClassRepository.findById(4L)
                .orElseThrow(() -> new AssertionError("FitnessClass 4 not found"));
        assertTrue(fitnessClass4.getTrainees().stream().noneMatch(t -> t.getId() == 1L));
    }

    @Test
    void delete_ShouldRemoveTraineeAndClearAssociations() throws Exception {
        // Act
        mockMvc.perform(delete("/trainee/1"))
                .andExpect(status().isNoContent());

        // Verify Trainee deletion
        assertFalse(traineeRepository.existsById(1L));

        // Verify FitnessClass associations are cleared
        List<FitnessClass> allFitnessClasses = (List<FitnessClass>) fitnessClassRepository.findAll();
        allFitnessClasses.forEach(fitnessClass ->
                assertTrue(fitnessClass.getTrainees().stream().noneMatch(t -> t.getId() == 1L),
                        "FitnessClass " + fitnessClass.getId() + " should not have Trainee 1L"));
    }

    @Test
    void readAll_ShouldReturnAllTrainees() throws Exception {
        mockMvc.perform(get("/trainee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void readById_ShouldReturnTrainee() throws Exception {
        mockMvc.perform(get("/trainee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findByFitnessClassId_ShouldReturnAssociatedTrainees() throws Exception {
        mockMvc.perform(get("/trainee/fitness-class/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").isNotEmpty());
    }
}
