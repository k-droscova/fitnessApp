package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.controller.mappers.ClassTypeMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.mappers.FitnessClassMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.mappers.InstructorMapper;
import cz.cvut.fit.tjv.fitnessApp.controller.mappers.RoomMapper;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.service.ClassTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/classtype")
public class ClassTypeController {

    private final ClassTypeService classTypeService;
    private final ClassTypeMapper classTypeMapper;
    private final InstructorMapper instructorMapper;
    private final RoomMapper roomMapper;
    private final FitnessClassMapper fitnessClassMapper;

    public ClassTypeController(ClassTypeService classTypeService) {
        this.classTypeService = classTypeService;
        this.classTypeMapper = ClassTypeMapper.INSTANCE;
        this.instructorMapper = InstructorMapper.INSTANCE;
        this.roomMapper = RoomMapper.INSTANCE;
        this.fitnessClassMapper = FitnessClassMapper.INSTANCE;
    }

    @Operation(summary = "Create a new ClassType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ClassType created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ClassType create(@RequestBody ClassType classType) {
        return classTypeService.create(classType);
    }

    @Operation(summary = "Update an existing ClassType by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "ClassType updated successfully"),
            @ApiResponse(responseCode = "404", description = "ClassType not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Integer id, @RequestBody ClassType data) {
        classTypeService.update(id, data);
    }

    @Operation(summary = "Retrieve all ClassTypes or search by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of ClassTypes retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid search query")
    })
    @GetMapping
    public Iterable<ClassType> readAllOrByName(@RequestParam Optional<String> name) {
        if (name.isPresent()) {
            return classTypeService.readAllByName(name.get());
        } else {
            return classTypeService.readAll();
        }
    }

    @Operation(summary = "Retrieve a ClassType by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ClassType retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "ClassType not found")
    })
    @GetMapping("/{id}")
    public ClassType readById(@PathVariable Integer id) {
        return classTypeService.readById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "ClassType not found"));
    }

    @Operation(summary = "Delete a ClassType by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "ClassType deleted successfully"),
            @ApiResponse(responseCode = "404", description = "ClassType not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        try {
            classTypeService.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ClassType not found");
        }
    }

    @Operation(summary = "Retrieve instructors by ClassType ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of instructors retrieved"),
            @ApiResponse(responseCode = "404", description = "ClassType not found")
    })
    @GetMapping("/{id}/instructors")
    public Set<Instructor> getInstructorsByClassType(@PathVariable Integer id) {
        return classTypeService.findInstructorsByClassType(id);
    }

    @Operation(summary = "Retrieve rooms by ClassType ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of rooms retrieved"),
            @ApiResponse(responseCode = "404", description = "ClassType not found")
    })
    @GetMapping("/{id}/rooms")
    public Set<Room> getRoomsByClassType(@PathVariable Integer id) {
        return classTypeService.findRoomsByClassType(id);
    }

    @Operation(summary = "Retrieve fitness classes by ClassType ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of fitness classes retrieved"),
            @ApiResponse(responseCode = "404", description = "ClassType not found")
    })
    @GetMapping("/{id}/fitness-classes")
    public Set<FitnessClass> getFitnessClassesByClassType(@PathVariable Integer id) {
        return classTypeService.findFitnessClassesByClassType(id);
    }
}