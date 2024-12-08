package cz.cvut.fit.tjv.fitnessApp.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.FitnessClassController;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.fitnessClass.CreateFitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.fitnessClass.FitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.service.FitnessClassService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.FitnessClassMapper;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FitnessClassController.class)
class FitnessClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FitnessClassService fitnessClassService;

    @MockBean
    private FitnessClassMapper fitnessClassMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private FitnessClass mockFitnessClass;
    private FitnessClassDto mockFitnessClassDto;
    private CreateFitnessClassDto mockCreateFitnessClassDto;
    private List<FitnessClass> mockFitnessClassList;
    private List<FitnessClassDto> mockFitnessClassDtoList;

    @BeforeEach
    void setUp() {
        mockFitnessClass = new FitnessClass();
        mockFitnessClass.setId(1L);

        mockFitnessClassDto = new FitnessClassDto();
        mockFitnessClassDto.setId(1L);

        mockCreateFitnessClassDto = new CreateFitnessClassDto();
        mockCreateFitnessClassDto.setCapacity(20);

        mockFitnessClassList = List.of(mockFitnessClass);
        mockFitnessClassDtoList = List.of(mockFitnessClassDto);
    }

    @AfterEach
    void tearDown() {
        mockFitnessClass = null;
        mockFitnessClassDto = null;
        mockCreateFitnessClassDto = null;
        mockFitnessClassList = null;
        mockFitnessClassDtoList = null;
    }

    @Test
    void create_ShouldReturnCreatedFitnessClass() throws Exception {
        Mockito.when(fitnessClassMapper.convertToEntity(any(CreateFitnessClassDto.class))).thenReturn(mockFitnessClass);
        Mockito.doNothing().when(fitnessClassService).scheduleClass(any(FitnessClass.class));
        Mockito.when(fitnessClassMapper.convertToDto(any(FitnessClass.class))).thenReturn(mockFitnessClassDto);

        mockMvc.perform(post("/fitness-class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateFitnessClassDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenRequestBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/fitness-class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid json\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.containsErrorMessage("Malformed JSON request"));
    }

    @Test
    void update_ShouldReturnNoContent() throws Exception {
        Mockito.when(fitnessClassMapper.convertToEntity(any(FitnessClassDto.class))).thenReturn(mockFitnessClass);
        Mockito.doNothing().when(fitnessClassService).validateAndUpdate(anyLong(), any(FitnessClass.class));

        mockMvc.perform(put("/fitness-class/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockFitnessClassDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenMapperFails() throws Exception {
        Mockito.when(fitnessClassMapper.convertToEntity(any(FitnessClassDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid FitnessClassDto"));

        mockMvc.perform(put("/fitness-class/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockFitnessClassDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid FitnessClassDto"));
    }

    @Test
    void readAllWithFilters_ShouldReturnAllFitnessClasses() throws Exception {
        Mockito.when(fitnessClassService.readAll()).thenReturn(mockFitnessClassList);
        Mockito.when(fitnessClassMapper.convertManyToDto(mockFitnessClassList)).thenReturn(mockFitnessClassDtoList);

        mockMvc.perform(get("/fitness-class"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void readById_ShouldReturnFitnessClass() throws Exception {
        Mockito.when(fitnessClassService.readById(1L)).thenReturn(Optional.of(mockFitnessClass));
        Mockito.when(fitnessClassMapper.convertToDto(mockFitnessClass)).thenReturn(mockFitnessClassDto);

        mockMvc.perform(get("/fitness-class/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void readById_ShouldReturnBadRequest() throws Exception {
        Mockito.when(fitnessClassService.readById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/fitness-class/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(fitnessClassService).deleteById(1L);

        mockMvc.perform(delete("/fitness-class/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void addTraineeToClass_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(fitnessClassService).addTraineeToClass(1L, 2L);

        mockMvc.perform(post("/fitness-class/1/add-trainee/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void addTraineeToClass_ShouldReturnBadRequest_WhenServiceFails() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Invalid Trainee")).when(fitnessClassService).addTraineeToClass(1L, 2L);

        mockMvc.perform(post("/fitness-class/1/add-trainee/2"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid Trainee"));
    }

    @Test
    void getTraineesByFitnessClass_ShouldReturnTrainees() throws Exception {
        Trainee mockTrainee = new Trainee();
        mockTrainee.setId(2L);
        Mockito.when(fitnessClassService.findTraineesById(1L)).thenReturn(List.of(mockTrainee));

        mockMvc.perform(get("/fitness-class/1/trainees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(2L));
    }

    @Test
    void getTraineesByFitnessClass_ShouldReturnBadRequest() throws Exception {
        Mockito.when(fitnessClassService.findTraineesById(1L)).thenThrow(new IllegalArgumentException("FitnessClass not found"));

        mockMvc.perform(get("/fitness-class/1/trainees"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: FitnessClass not found"));
    }

    @Test
    void removeTraineeFromClass_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(fitnessClassService).deleteTraineeFromClass(1L, 2L);

        mockMvc.perform(delete("/fitness-class/1/remove-trainee/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeTraineeFromClass_ShouldReturnBadRequest_WhenServiceFails() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Invalid Trainee or FitnessClass"))
                .when(fitnessClassService).deleteTraineeFromClass(1L, 2L);

        mockMvc.perform(delete("/fitness-class/1/remove-trainee/2"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid Trainee or FitnessClass"));
    }
}