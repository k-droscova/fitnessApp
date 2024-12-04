package cz.cvut.fit.tjv.fitnessApp.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.instructor.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
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
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InstructorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private ClassTypeRepository classTypeRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Test
    void create_ShouldPersistInstructor() throws Exception {
        // Arrange
        InstructorDto instructorDto = new InstructorDto();
        instructorDto.setName("New");
        instructorDto.setSurname("Instructor");
        instructorDto.setBirthDate(LocalDate.of(1990, 1, 1));
        instructorDto.setClassTypeIds(List.of(1L)); // Preloaded ClassType IDs
        instructorDto.setFitnessClassIds(List.of(1L));

        // Act
        MvcResult result = mockMvc.perform(post("/instructor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(instructorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        // Verify persistence
        InstructorDto createdDto = objectMapper.readValue(result.getResponse().getContentAsString(), InstructorDto.class);
        assertNotNull(createdDto.getId());
        assertEquals("New", createdDto.getName());
        assertEquals("Instructor", createdDto.getSurname());

        // Verify associations
        Instructor instructor = instructorRepository.findById(createdDto.getId())
                .orElseThrow(() -> new AssertionError("Instructor not found"));
        assertEquals(1, instructor.getSpecializations().size());
        assertTrue(instructor.getSpecializations().stream().anyMatch(ct -> ct.getId() == 1L));

        ClassType classtype = classTypeRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("ClassType not found"));
        assertTrue(classtype.getInstructors().stream().anyMatch(ct -> Objects.equals(ct.getId(), instructor.getId())));

        FitnessClass fitnessClass = fitnessClassRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("FitnessClass not found"));
        assertEquals(fitnessClass.getInstructor().getId(), instructor.getId());
    }

    @Test
    void update_ShouldModifyInstructorAndAssociations() throws Exception {
        // Arrange
        InstructorDto updateDto = new InstructorDto();
        updateDto.setName("Jane");
        updateDto.setSurname("Smith");
        updateDto.setBirthDate(LocalDate.of(1985, 5, 20));
        updateDto.setClassTypeIds(List.of(3L, 4L)); // New ClassType IDs

        // Act
        mockMvc.perform(put("/instructor/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        // Verify update
        Instructor updatedInstructor = instructorRepository.findById(1L)
                .orElseThrow(() -> new AssertionError("Instructor not found"));
        assertEquals("Jane", updatedInstructor.getName());
        assertEquals("Smith", updatedInstructor.getSurname());
        assertEquals(LocalDate.of(1985, 5, 20), updatedInstructor.getBirthDate());

        // Verify associations
        assertEquals(2, updatedInstructor.getSpecializations().size());
        assertTrue(updatedInstructor.getSpecializations().stream().anyMatch(ct -> ct.getId() == 3L));
        assertTrue(updatedInstructor.getSpecializations().stream().anyMatch(ct -> ct.getId() == 4L));

        // Verify reverse association
        ClassType classType = classTypeRepository.findById(3L)
                .orElseThrow(() -> new AssertionError("ClassType not found"));
        assertTrue(classType.getInstructors().stream().anyMatch(i -> i.getId() == 1L));
    }

    @Test
    void deleteById_ShouldRemoveInstructorAndUpdateAssociations() throws Exception {
        // Act
        mockMvc.perform(delete("/instructor/1"))
                .andExpect(status().isNoContent());

        // Verify removal
        assertFalse(instructorRepository.existsById(1L));

        // Verify associations are updated
        List<ClassType> classTypes = (List<ClassType>) classTypeRepository.findAll();
        for (ClassType classType : classTypes) {
            assertTrue(classType.getInstructors().stream().noneMatch(i -> i.getId() == 1L));
        }

        // Verify FitnessClass associations
        List<FitnessClass> fitnessClasses = (List<FitnessClass>) fitnessClassRepository.findAll();
        for (FitnessClass fitnessClass : fitnessClasses) {
            assertNotEquals(1L, fitnessClass.getInstructor().getId());
        }
    }

    @Test
    void readAllOrSearch_ShouldReturnInstructors() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/instructor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4)); // Assuming 4 instructors in test data
    }

    @Test
    void readById_ShouldReturnInstructor() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/instructor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    void readById_ShouldReturnNotFound_ForInvalidId() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/instructor/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAvailableInstructors_ShouldReturnAvailableInstructors() throws Exception {
        // Act
        mockMvc.perform(get("/instructor/available")
                        .param("date", "2024-12-05")
                        .param("time", "10:00:00")
                        .param("classTypeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}