package cz.cvut.fit.tjv.fitnessApp.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.ClassTypeController;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.classType.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.classType.CreateClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.service.ClassTypeService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.ClassTypeMapper;
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

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassTypeController.class)
class ClassTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassTypeService classTypeService;

    @MockBean
    private ClassTypeMapper classTypeMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private ClassType mockClassType;
    private ClassTypeDto mockClassTypeDto;
    private CreateClassTypeDto mockCreateClassTypeDto;
    private List<ClassType> mockClassTypeList;
    private List<ClassTypeDto> mockClassTypeDtoList;

    @BeforeEach
    void setUp() {
        mockClassType = new ClassType();
        mockClassType.setId(1L);
        mockClassType.setName("Yoga");

        mockClassTypeDto = new ClassTypeDto();
        mockClassTypeDto.setId(1L);
        mockClassTypeDto.setName("Yoga");

        mockCreateClassTypeDto = new CreateClassTypeDto();
        mockCreateClassTypeDto.setName("Yoga");

        mockClassTypeList = List.of(mockClassType);
        mockClassTypeDtoList = List.of(mockClassTypeDto);
    }

    @AfterEach
    void tearDown() {
        mockClassType = null;
        mockClassTypeDto = null;
        mockCreateClassTypeDto = null;
        mockClassTypeList = null;
        mockClassTypeDtoList = null;
    }

    @Test
    void create_ShouldReturnCreatedClassType() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(CreateClassTypeDto.class))).thenReturn(mockClassType);
        Mockito.when(classTypeService.create(any(ClassType.class))).thenReturn(mockClassType);
        Mockito.when(classTypeMapper.convertToDto(mockClassType)).thenReturn(mockClassTypeDto);

        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateClassTypeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Yoga"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMapperFailsWithIllegalArgumentException() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(CreateClassTypeDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid CreateClassTypeDto"));

        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateClassTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid CreateClassTypeDto"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenServiceFailsWithIllegalArgumentException() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(CreateClassTypeDto.class))).thenReturn(mockClassType);
        Mockito.when(classTypeService.create(any(ClassType.class)))
                .thenThrow(new IllegalArgumentException("Invalid ClassType entity"));

        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateClassTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid ClassType entity"));
    }

    @Test
    void update_ShouldReturnNoContent() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(ClassTypeDto.class))).thenReturn(mockClassType);

        mockMvc.perform(put("/classtype/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockClassTypeDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenMapperFailsWithIllegalArgumentException() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(ClassTypeDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid ClassTypeDto"));

        mockMvc.perform(put("/classtype/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockClassTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid ClassTypeDto"));
    }

    @Test
    void update_ShouldReturnBadRequest_WhenServiceFailsWithIllegalArgumentException() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(ClassTypeDto.class))).thenReturn(mockClassType);
        Mockito.doThrow(new IllegalArgumentException("Invalid update operation"))
                .when(classTypeService).update(anyLong(), any(ClassType.class));

        mockMvc.perform(put("/classtype/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockClassTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid update operation"));
    }

    @Test
    void readAllOrByName_ShouldReturnClassTypes() throws Exception {
        Mockito.when(classTypeService.readAll()).thenReturn(mockClassTypeList);
        Mockito.when(classTypeMapper.convertManyToDto(any())).thenReturn(mockClassTypeDtoList);

        mockMvc.perform(get("/classtype"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Yoga"));
    }

    @Test
    void readById_ShouldReturnClassType() throws Exception {
        Mockito.when(classTypeService.readById(1L)).thenReturn(Optional.of(mockClassType));
        Mockito.when(classTypeMapper.convertToDto(mockClassType)).thenReturn(mockClassTypeDto);

        mockMvc.perform(get("/classtype/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Yoga"));
    }

    @Test
    void readById_ShouldReturnBadRequest_WhenServiceReturnsEmpty() throws Exception {
        Mockito.when(classTypeService.readById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/classtype/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/classtype/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(classTypeService).deleteById(1L);
    }

    @Test
    void deleteById_ShouldReturnBadRequest_WhenServiceFailsWithIllegalArgumentException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Cannot delete ClassType"))
                .when(classTypeService).deleteById(anyLong());

        mockMvc.perform(delete("/classtype/1"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Cannot delete ClassType"));
    }
}