package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.service.ClassTypeService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.ClassTypeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/classtype")
public class ClassTypeController {

    private final ClassTypeService classTypeService;
    private final ClassTypeMapper classTypeMapper;

    public ClassTypeController(ClassTypeService classTypeService, ClassTypeMapper classTypeMapper) {
        this.classTypeService = classTypeService;
        this.classTypeMapper = classTypeMapper;
    }

    @Operation(summary = "Create a new ClassType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ClassType created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ClassTypeDto create(@RequestBody ClassTypeDto classTypeDto) {
        ClassType classType = classTypeMapper.convertToEntity(classTypeDto);
        return classTypeMapper.convertToDto(classTypeService.create(classType));
    }

    @Operation(summary = "Update an existing ClassType by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "ClassType updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "ClassType not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody ClassTypeDto classTypeDto) {
        ClassType classType = classTypeMapper.convertToEntity(classTypeDto);
        classTypeService.update(id, classType);
    }

    @Operation(summary = "Retrieve all ClassTypes or search by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of ClassTypes retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid search query"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<ClassTypeDto> readAllOrByName(@RequestParam Optional<String> name) {
        if (name.isPresent()) {
            List<ClassType> classTypes = classTypeService.readAllByName(name.get());
            return classTypeMapper.convertManyToDto(new ArrayList<>(classTypes));
        } else {
            Iterable<ClassType> classTypes = classTypeService.readAll();
            return classTypeMapper.convertManyToDto(
                    StreamSupport.stream(classTypes.spliterator(), false).collect(Collectors.toList())
            );
        }
    }

    @Operation(summary = "Retrieve a ClassType by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ClassType retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "ClassType not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ClassTypeDto readById(@PathVariable Long id) {
        ClassType classType = classTypeService.readById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "ClassType not found"));
        return classTypeMapper.convertToDto(classType);
    }

    @Operation(summary = "Delete a ClassType by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "ClassType deleted successfully"),
            @ApiResponse(responseCode = "404", description = "ClassType not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        classTypeService.deleteById(id);
    }

    @Operation(summary = "Retrieve instructors by ClassType ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of instructors retrieved"),
            @ApiResponse(responseCode = "404", description = "ClassType not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/instructors")
    public Set<Long> getInstructorsByClassType(@PathVariable Long id) {
        return classTypeService.findInstructorsById(id).stream()
                .map(Instructor::getId)
                .collect(Collectors.toSet());
    }

    @Operation(summary = "Retrieve rooms by ClassType ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of rooms retrieved"),
            @ApiResponse(responseCode = "404", description = "ClassType not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/rooms")
    public Set<Long> getRoomsByClassType(@PathVariable Long id) {
        return classTypeService.findRoomsById(id).stream()
                .map(Room::getId)
                .collect(Collectors.toSet());
    }

    @Operation(summary = "Retrieve fitness classes by ClassType ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of fitness classes retrieved"),
            @ApiResponse(responseCode = "404", description = "ClassType not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/fitness-classes")
    public Set<Long> getFitnessClassesByClassType(@PathVariable Long id) {
        return classTypeService.findFitnessClassesById(id).stream()
                .map(FitnessClass::getId)
                .collect(Collectors.toSet());
    }
}