package cz.cvut.fit.tjv.fitnessApp.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.InstructorController;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.instructor.CreateInstructorDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.instructor.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.service.InstructorService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.InstructorMapper;
import cz.cvut.fit.tjv.fitnessApp.testUtils.ErrorMatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorController.class)
class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstructorService instructorService;

    @MockBean
    private InstructorMapper instructorMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Instructor mockInstructor;
    private InstructorDto mockInstructorDto;
    private CreateInstructorDto mockCreateInstructorDto;
    private List<Instructor> mockInstructorList;
    private List<InstructorDto> mockInstructorDtoList;

    @BeforeEach
    void setUp() {
        mockInstructor = new Instructor();
        mockInstructor.setId(1L);
        mockInstructor.setName("John");
        mockInstructor.setSurname("Doe");

        mockInstructorDto = new InstructorDto();
        mockInstructorDto.setId(1L);
        mockInstructorDto.setName("John");
        mockInstructorDto.setSurname("Doe");

        mockCreateInstructorDto = new CreateInstructorDto();
        mockCreateInstructorDto.setName("John");
        mockCreateInstructorDto.setSurname("Doe");

        mockInstructorList = List.of(mockInstructor);
        mockInstructorDtoList = List.of(mockInstructorDto);
    }

    @AfterEach
    void tearDown() {
        mockInstructor = null;
        mockInstructorDto = null;
        mockCreateInstructorDto = null;
        mockInstructorList = null;
        mockInstructorDtoList = null;
    }

    @Test
    void create_ShouldReturnCreatedInstructor() throws Exception {
        Mockito.when(instructorMapper.convertToEntity(any(CreateInstructorDto.class))).thenReturn(mockInstructor);
        Mockito.when(instructorService.create(any(Instructor.class))).thenReturn(mockInstructor);
        Mockito.when(instructorMapper.convertToDto(mockInstructor)).thenReturn(mockInstructorDto);

        mockMvc.perform(post("/instructor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateInstructorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenRequestBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/instructor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid json\"}")) // Malformed JSON
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.containsErrorMessage("Malformed JSON request"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMapperFails() throws Exception {
        Mockito.when(instructorMapper.convertToEntity(any(CreateInstructorDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid InstructorDto"));

        mockMvc.perform(post("/instructor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateInstructorDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid InstructorDto"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenDuplicateInstructor() throws Exception {
        Mockito.when(instructorMapper.convertToEntity(any(CreateInstructorDto.class))).thenReturn(mockInstructor);
        Mockito.when(instructorService.create(any(Instructor.class)))
                .thenThrow(new IllegalArgumentException("Instructor already exists"));

        mockMvc.perform(post("/instructor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateInstructorDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Instructor already exists"));
    }

    @Test
    void update_ShouldReturnNoContent() throws Exception {
        Mockito.when(instructorMapper.convertToEntity(any(InstructorDto.class))).thenReturn(mockInstructor);

        mockMvc.perform(put("/instructor/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockInstructorDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenMapperFails() throws Exception {
        Mockito.when(instructorMapper.convertToEntity(any(InstructorDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid InstructorDto"));

        mockMvc.perform(put("/instructor/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockInstructorDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid InstructorDto"));
    }

    @Test
    void readAllOrSearch_ShouldReturnAllInstructors() throws Exception {
        Mockito.when(instructorService.readAll()).thenReturn(mockInstructorList);
        Mockito.when(instructorMapper.convertManyToDto(mockInstructorList)).thenReturn(mockInstructorDtoList);

        mockMvc.perform(get("/instructor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Doe"));
    }

    @Test
    void readById_ShouldReturnInstructor() throws Exception {
        Mockito.when(instructorService.readById(1L)).thenReturn(Optional.of(mockInstructor));
        Mockito.when(instructorMapper.convertToDto(mockInstructor)).thenReturn(mockInstructorDto);

        mockMvc.perform(get("/instructor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void readById_ShouldReturnNotFound() throws Exception {
        Mockito.when(instructorService.readById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/instructor/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/instructor/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(instructorService).deleteById(1L);
    }

    @Test
    void findAvailableInstructors_ShouldReturnAvailableInstructors() throws Exception {
        Mockito.when(instructorService.findAvailableInstructors(
                eq(Optional.of(1L)),
                any(LocalDate.class),
                any(LocalTime.class)
        )).thenReturn(mockInstructorList);

        mockMvc.perform(get("/instructor/available")
                        .param("classTypeId", "1")
                        .param("date", "2024-12-01")
                        .param("time", "10:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1L));
    }

    @Test
    void findAvailableInstructors_ShouldReturnBadRequest_WhenDateIsMissing() throws Exception {
        mockMvc.perform(get("/instructor/available")
                        .param("time", "10:00"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Missing required parameter: date"));
    }
}