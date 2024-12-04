package cz.cvut.fit.tjv.fitnessApp.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.RoomController;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.room.CreateRoomDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.room.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.service.RoomService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.RoomMapper;
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

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private RoomMapper roomMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Room mockRoom;
    private RoomDto mockRoomDto;
    private CreateRoomDto mockCreateRoomDto;
    private List<Room> mockRoomList;
    private List<RoomDto> mockRoomDtoList;

    @BeforeEach
    void setUp() {
        mockRoom = new Room();
        mockRoom.setId(1L);
        mockRoom.setMaxCapacity(50);

        mockRoomDto = new RoomDto();
        mockRoomDto.setId(1L);
        mockRoomDto.setMaxCapacity(50);

        mockCreateRoomDto = new CreateRoomDto();
        mockCreateRoomDto.setMaxCapacity(50);

        mockRoomList = List.of(mockRoom);
        mockRoomDtoList = List.of(mockRoomDto);
    }

    @AfterEach
    void tearDown() {
        mockRoom = null;
        mockRoomDto = null;
        mockCreateRoomDto = null;
        mockRoomList = null;
        mockRoomDtoList = null;
    }

    @Test
    void create_ShouldReturnCreatedRoom() throws Exception {
        Mockito.when(roomMapper.convertToEntity(any(CreateRoomDto.class))).thenReturn(mockRoom);
        Mockito.when(roomService.create(any(Room.class))).thenReturn(mockRoom);
        Mockito.when(roomMapper.convertToDto(mockRoom)).thenReturn(mockRoomDto);

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateRoomDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.maxCapacity").value(50));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenRequestBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid json\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.containsErrorMessage("Malformed JSON request"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenMapperFails() throws Exception {
        Mockito.when(roomMapper.convertToEntity(any(CreateRoomDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid RoomDto"));

        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateRoomDto)))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Invalid argument: Invalid RoomDto"));
    }

    @Test
    void update_ShouldReturnNoContent() throws Exception {
        Mockito.when(roomMapper.convertToEntity(any(RoomDto.class))).thenReturn(mockRoom);

        mockMvc.perform(put("/room/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRoomDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void readAll_ShouldReturnAllRooms() throws Exception {
        Mockito.when(roomService.readAll()).thenReturn(mockRoomList);
        Mockito.when(roomMapper.convertManyToDto(mockRoomList)).thenReturn(mockRoomDtoList);

        mockMvc.perform(get("/room"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].maxCapacity").value(50));
    }

    @Test
    void readAll_ShouldReturnEmptyList_WhenNoRoomsExist() throws Exception {
        Mockito.when(roomService.readAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/room"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void findAvailableRooms_ShouldReturnAvailableRooms() throws Exception {
        Mockito.when(roomService.findAvailableRooms(
                eq(Optional.of(1L)),
                any(LocalDate.class),
                any(LocalTime.class)
        )).thenReturn(mockRoomList);

        mockMvc.perform(get("/room/available")
                        .param("classTypeId", "1")
                        .param("date", "2024-12-01")
                        .param("time", "10:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1L));
    }

    @Test
    void findAvailableRooms_ShouldReturnBadRequest_WhenDateIsMissing() throws Exception {
        mockMvc.perform(get("/room/available")
                        .param("time", "10:00"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Missing required parameter: date"));
    }

    @Test
    void findAvailableRooms_ShouldReturnBadRequest_WhenTimeIsMissing() throws Exception {
        mockMvc.perform(get("/room/available")
                        .param("date", "2024-12-01"))
                .andExpect(status().isBadRequest())
                .andExpect(ErrorMatcher.matchesErrorMessage("Missing required parameter: time"));
    }

    @Test
    void readById_ShouldReturnRoom() throws Exception {
        Mockito.when(roomService.readById(1L)).thenReturn(Optional.of(mockRoom));
        Mockito.when(roomMapper.convertToDto(mockRoom)).thenReturn(mockRoomDto);

        mockMvc.perform(get("/room/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.maxCapacity").value(50));
    }

    @Test
    void readById_ShouldReturnNotFound() throws Exception {
        Mockito.when(roomService.readById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/room/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/room/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(roomService).deleteById(1L);
    }
}