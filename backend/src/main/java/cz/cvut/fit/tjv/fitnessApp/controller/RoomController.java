package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.service.RoomService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.RoomMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    public RoomController(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    @Operation(summary = "Create a new Room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public RoomDto create(@RequestBody RoomDto roomDto) {
        Room room = roomMapper.convertToEntity(roomDto);
        return roomMapper.convertToDto(roomService.create(room));
    }

    @Operation(summary = "Update an existing Room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        Room room = roomMapper.convertToEntity(roomDto);
        roomService.update(id, room);
    }

    @Operation(summary = "Retrieve all Rooms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Rooms retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<RoomDto> readAll() {
        List<Room> rooms = roomService.readAll();
        return roomMapper.convertManyToDto(rooms);
    }

    @Operation(summary = "Retrieve a Room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public RoomDto readById(@PathVariable Long id) {
        Room room = roomService.readById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        return roomMapper.convertToDto(room);
    }

    @Operation(summary = "Delete a Room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Room not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        roomService.deleteById(id);
    }

    @Operation(summary = "Retrieve available Rooms for a given date, time, and optional ClassType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of available Rooms retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/available")
    public List<Long> findAvailableRooms(
            @RequestParam Optional<Long> classTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        return roomService.findAvailableRooms(classTypeId, date, time).stream()
                .map(Room::getId)
                .collect(Collectors.toList());
    }
}
