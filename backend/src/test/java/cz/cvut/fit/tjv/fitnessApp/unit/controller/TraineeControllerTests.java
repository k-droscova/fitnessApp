package cz.cvut.fit.tjv.fitnessApp.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.TraineeController;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.trainee.CreateTraineeDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.trainee.TraineeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.service.TraineeService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.TraineeMapper;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TraineeService traineeService;

    @MockBean
    private TraineeMapper traineeMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Trainee mockTrainee;
    private TraineeDto mockTraineeDto;
    private CreateTraineeDto mockCreateTraineeDto;
    private List<Trainee> mockTraineeList;
    private List<TraineeDto> mockTraineeDtoList;

    @BeforeEach
    void setUp() {
        mockTrainee = new Trainee();
        mockTrainee.setId(1L);
        mockTrainee.setName("Jane");
        mockTrainee.setSurname("Doe");

        mockTraineeDto = new TraineeDto();
        mockTraineeDto.setId(1L);
        mockTraineeDto.setName("Jane");
        mockTraineeDto.setSurname("Doe");

        mockCreateTraineeDto = new CreateTraineeDto();
        mockCreateTraineeDto.setName("Jane");
        mockCreateTraineeDto.setSurname("Doe");

        mockTraineeList = List.of(mockTrainee);
        mockTraineeDtoList = List.of(mockTraineeDto);
    }

    @AfterEach
    void tearDown() {
        mockTrainee = null;
        mockTraineeDto = null;
        mockCreateTraineeDto = null;
        mockTraineeList = null;
        mockTraineeDtoList = null;
    }

    @Test
    void create_ShouldReturnCreatedTrainee() throws Exception {
        Mockito.when(traineeMapper.convertToEntity(any(CreateTraineeDto.class))).thenReturn(mockTrainee);
        Mockito.when(traineeService.create(any(Trainee.class))).thenReturn(mockTrainee);
        Mockito.when(traineeMapper.convertToDto(mockTrainee)).thenReturn(mockTraineeDto);

        mockMvc.perform(post("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateTraineeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMapperFails() throws Exception {
        Mockito.when(traineeMapper.convertToEntity(any(CreateTraineeDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid TraineeDto"));

        mockMvc.perform(post("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateTraineeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid TraineeDto"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenRequestBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid json\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.containsErrorMessage("Malformed JSON request"));
    }

    @Test
    void update_ShouldReturnNoContent() throws Exception {
        Mockito.when(traineeMapper.convertToEntity(any(TraineeDto.class))).thenReturn(mockTrainee);

        mockMvc.perform(put("/trainee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockTraineeDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenMapperFails() throws Exception {
        Mockito.when(traineeMapper.convertToEntity(any(TraineeDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid TraineeDto"));

        mockMvc.perform(put("/trainee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockTraineeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid TraineeDto"));
    }

    @Test
    void readAll_ShouldReturnAllTrainees() throws Exception {
        Mockito.when(traineeService.readAll()).thenReturn(mockTraineeList);
        Mockito.when(traineeMapper.convertManyToDto(mockTraineeList)).thenReturn(mockTraineeDtoList);

        mockMvc.perform(get("/trainee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Jane"))
                .andExpect(jsonPath("$[0].surname").value("Doe"));
    }

    @Test
    void readById_ShouldReturnTrainee() throws Exception {
        Mockito.when(traineeService.readById(1L)).thenReturn(Optional.of(mockTrainee));
        Mockito.when(traineeMapper.convertToDto(mockTrainee)).thenReturn(mockTraineeDto);

        mockMvc.perform(get("/trainee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void readById_ShouldReturnBadRequest() throws Exception {
        Mockito.when(traineeService.readById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/trainee/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/trainee/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(traineeService).deleteById(1L);
    }

    @Test
    void findByFitnessClassId_ShouldReturnTrainees() throws Exception {
        Mockito.when(traineeService.findTraineesByFitnessClassId(1L)).thenReturn(mockTraineeList);
        Mockito.when(traineeMapper.convertManyToDto(mockTraineeList)).thenReturn(mockTraineeDtoList);

        mockMvc.perform(get("/trainee/fitness-class/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void findByFitnessClassId_ShouldReturnEmptyList_WhenNoTraineesFound() throws Exception {
        Mockito.when(traineeService.findTraineesByFitnessClassId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/trainee/fitness-class/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchTraineesByName_ShouldReturnMatchingTrainees() throws Exception {
        Mockito.when(traineeService.findTraineesByName("J"))
                .thenReturn(mockTraineeList);
        Mockito.when(traineeMapper.convertManyToDto(mockTraineeList))
                .thenReturn(mockTraineeDtoList);

        mockMvc.perform(get("/trainee/search")
                        .param("input", "J"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Jane"))
                .andExpect(jsonPath("$[0].surname").value("Doe"));
    }

    @Test
    void searchTraineesByName_ShouldReturnEmptyList_WhenNoMatchesFound() throws Exception {
        Mockito.when(traineeService.findTraineesByName("NonExistent"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/trainee/search")
                        .param("input", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchTraineesByName_ShouldReturnBadRequest_WhenInputIsMissing() throws Exception {
        mockMvc.perform(get("/trainee/search"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.containsErrorMessage("input"));
    }
}