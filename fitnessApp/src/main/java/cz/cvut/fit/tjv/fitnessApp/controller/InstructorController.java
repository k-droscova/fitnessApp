package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.service.InstructorService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.InstructorMapper;
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
@RequestMapping("/instructor")
public class InstructorController {

    private final InstructorService instructorService;
    private final InstructorMapper instructorMapper;

    public InstructorController(InstructorService instructorService, InstructorMapper instructorMapper) {
        this.instructorService = instructorService;
        this.instructorMapper = instructorMapper;
    }

    @Operation(summary = "Create a new Instructor")
    @PostMapping
    public InstructorDto create(@RequestBody InstructorDto instructorDto) {
        Instructor instructor = instructorMapper.convertToEntity(instructorDto);
        return instructorMapper.convertToDto(instructorService.create(instructor));
    }

    @Operation(summary = "Update an existing Instructor by ID")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody InstructorDto instructorDto) {
        Instructor instructor = instructorMapper.convertToEntity(instructorDto);
        instructorService.update(id, instructor);
    }

    @Operation(summary = "Retrieve all Instructors or search by name/surname")
    @GetMapping
    public List<InstructorDto> readAllOrSearch(
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> surname,
            @RequestParam Optional<String> input) {
        List<Instructor> instructors;
        if (name.isPresent()) {
            instructors = instructorService.readAllByName(name.get());
        } else if (surname.isPresent()) {
            instructors = instructorService.readAllBySurname(surname.get());
        } else if (input.isPresent()) {
            instructors = instructorService.readAllByNameOrSurname(input.get());
        } else {
            instructors = instructorService.readAll();
        }
        return instructorMapper.convertManyToDto(instructors);
    }

    @Operation(summary = "Retrieve an Instructor by ID")
    @GetMapping("/{id}")
    public InstructorDto readById(@PathVariable Long id) {
        Instructor instructor = instructorService.readById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found"));
        return instructorMapper.convertToDto(instructor);
    }

    @Operation(summary = "Delete an Instructor by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        instructorService.deleteById(id);
    }

    @Operation(summary = "Retrieve available Instructors for a given date, time, and optional ClassType")
    @GetMapping("/available")
    public List<Long> findAvailableInstructors(
            @RequestParam Optional<Long> classTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        return instructorService.findAvailableInstructors(classTypeId, date, time).stream()
                .map(Instructor::getId)
                .collect(Collectors.toList());
    }
}
