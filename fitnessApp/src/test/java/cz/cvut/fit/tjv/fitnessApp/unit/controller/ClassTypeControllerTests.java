package cz.cvut.fit.tjv.fitnessApp.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.ClassTypeController;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.service.ClassTypeService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.ClassTypeMapper;
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
    private List<ClassType> mockClassTypeList;
    private List<ClassTypeDto> mockClassTypeDtoList;

    private Instructor mockInstructor1;
    private Instructor mockInstructor2;
    private Room mockRoom1;
    private Room mockRoom2;
    private FitnessClass mockFitnessClass1;
    private FitnessClass mockFitnessClass2;

    @BeforeEach
    void setUp() {
        mockClassType = new ClassType();
        mockClassType.setId(1L);
        mockClassType.setName("Yoga");

        mockClassTypeDto = new ClassTypeDto();
        mockClassTypeDto.setId(1L);
        mockClassTypeDto.setName("Yoga");

        mockInstructor1 = new Instructor();
        mockInstructor1.setId(1L);
        mockInstructor1.setName("John");
        mockInstructor1.setSurname("Doe");

        mockInstructor2 = new Instructor();
        mockInstructor2.setId(2L);
        mockInstructor2.setName("Jane");
        mockInstructor2.setSurname("Smith");

        mockRoom1 = new Room();
        mockRoom1.setId(1L);
        mockRoom1.setMaxCapacity(20);

        mockRoom2 = new Room();
        mockRoom2.setId(2L);
        mockRoom2.setMaxCapacity(15);

        mockFitnessClass1 = new FitnessClass();
        mockFitnessClass1.setId(1L);

        mockFitnessClass2 = new FitnessClass();
        mockFitnessClass2.setId(2L);

        mockClassTypeList = List.of(mockClassType);
        mockClassTypeDtoList = List.of(mockClassTypeDto);
    }

    @AfterEach
    void tearDown() {
        mockClassType = null;
        mockClassTypeDto = null;
        mockClassTypeList = null;
        mockClassTypeDtoList = null;
        mockInstructor1 = null;
        mockInstructor2 = null;
        mockRoom1 = null;
        mockRoom2 = null;
        mockFitnessClass1 = null;
        mockFitnessClass2 = null;
    }

    @Test
    void create_ShouldReturnCreatedClassType() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(ClassTypeDto.class))).thenReturn(mockClassType);
        Mockito.when(classTypeService.create(any(ClassType.class))).thenReturn(mockClassType);
        Mockito.when(classTypeMapper.convertToDto(mockClassType)).thenReturn(mockClassTypeDto);

        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockClassTypeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Yoga"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMapperFailsWithIllegalArgumentException() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(ClassTypeDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid ClassTypeDto"));

        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockClassTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid argument: Invalid ClassTypeDto"));
    }

    @Test
    void create_ShouldReturnInternalServerError_WhenServiceFailsWithNonIllegalArgumentException() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(ClassTypeDto.class))).thenReturn(mockClassType);
        Mockito.when(classTypeService.create(any(ClassType.class)))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockClassTypeDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("A runtime error occurred: Service error"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenServiceFailsWithIllegalArgumentException() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(ClassTypeDto.class))).thenReturn(mockClassType);
        Mockito.when(classTypeService.create(any(ClassType.class)))
                .thenThrow(new IllegalArgumentException("Invalid ClassType entity"));

        mockMvc.perform(post("/classtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockClassTypeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid argument: Invalid ClassType entity"));
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
                .andExpect(content().string("Invalid argument: Invalid ClassTypeDto"));
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
                .andExpect(content().string("Invalid argument: Invalid update operation"));
    }

    @Test
    void update_ShouldReturnInternalServerError_WhenServiceFailsWithNonIllegalArgumentException() throws Exception {
        Mockito.when(classTypeMapper.convertToEntity(any(ClassTypeDto.class))).thenReturn(mockClassType);
        Mockito.doThrow(new RuntimeException("Service error")).when(classTypeService).update(anyLong(), any());

        mockMvc.perform(put("/classtype/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockClassTypeDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("A runtime error occurred: Service error"));
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
    void readById_ShouldReturnNotFound_WhenServiceFails() throws Exception {
        Mockito.when(classTypeService.readById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/classtype/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/classtype/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(classTypeService).deleteById(1L);
    }

    @Test
    void deleteById_ShouldReturnInternalServerError_WhenServiceFails() throws Exception {
        Mockito.doThrow(new RuntimeException("Service error")).when(classTypeService).deleteById(anyLong());

        mockMvc.perform(delete("/classtype/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("A runtime error occurred: Service error"));
    }

    @Test
    void deleteById_ShouldReturnBadRequest_WhenServiceFailsWithIllegalArgumentException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Cannot delete ClassType"))
                .when(classTypeService).deleteById(anyLong());

        mockMvc.perform(delete("/classtype/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid argument: Cannot delete ClassType"));
    }
}