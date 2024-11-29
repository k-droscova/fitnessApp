package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.service.RoomService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.RoomMapper;
import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping
    public RoomDto create(@RequestBody RoomDto roomDto) {
        Room room = roomMapper.convertToEntity(roomDto);
        return roomMapper.convertToDto(roomService.create(room));
    }

    @Operation(summary = "Update an existing Room by ID")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        Room room = roomMapper.convertToEntity(roomDto);
        roomService.update(id, room);
    }

    @Operation(summary = "Retrieve all Rooms")
    @GetMapping
    public List<RoomDto> readAll() {
        List<Room> rooms = roomService.readAll();
        return roomMapper.convertManyToDto(rooms);
    }

    @Operation(summary = "Retrieve a Room by ID")
    @GetMapping("/{id}")
    public RoomDto readById(@PathVariable Long id) {
        Room room = roomService.readById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        return roomMapper.convertToDto(room);
    }

    @Operation(summary = "Delete a Room by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        roomService.deleteById(id);
    }

    @Operation(summary = "Retrieve available Rooms for a given date, time, and optional ClassType")
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
