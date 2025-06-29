package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.room.CreateRoomDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.room.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.errorHandling.ErrorResponseImpl;
import cz.cvut.fit.tjv.fitnessApp.service.RoomService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.RoomMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/room", produces = MediaType.APPLICATION_JSON_VALUE)
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
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
    })
    @PostMapping
    public RoomDto create(@RequestBody CreateRoomDto roomDto) {
        Room room = roomMapper.convertToEntity(roomDto);
        return roomMapper.convertToDto(roomService.create(room));
    }

    @Operation(summary = "Update an existing Room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
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
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
    })
    @GetMapping
    public List<RoomDto> readAll() {
        List<Room> rooms = roomService.readAll();
        return roomMapper.convertManyToDto(rooms);
    }

    @Operation(summary = "Retrieve a Room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request (e.g., Room not found)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
    })
    @GetMapping("/{id}")
    public RoomDto readById(@PathVariable Long id) {
        Room room = roomService.readById(id).orElseThrow(() ->
                new IllegalArgumentException("Room not found for ID: " + id));
        return roomMapper.convertToDto(room);
    }

    @Operation(summary = "Delete a Room by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        roomService.deleteById(id);
    }

    @Operation(summary = "Retrieve available Rooms for a given date, time, and optional ClassType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of available Rooms retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
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

    @Operation(summary = "Search Rooms by ClassType name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Rooms retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
    })
    @GetMapping("/search")
    public List<RoomDto> findByClassTypeName(@RequestParam String classTypeName) {
        List<Room> rooms = roomService.findByClassTypeName(classTypeName);
        return roomMapper.convertManyToDto(rooms);
    }
}
