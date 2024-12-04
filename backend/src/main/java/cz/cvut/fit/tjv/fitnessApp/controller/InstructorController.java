package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.instructor.CreateInstructorDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.instructor.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.errorHandling.ErrorResponseImpl;
import cz.cvut.fit.tjv.fitnessApp.service.InstructorService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.InstructorMapper;
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
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/instructor", produces = MediaType.APPLICATION_JSON_VALUE)
public class InstructorController {

    private final InstructorService instructorService;
    private final InstructorMapper instructorMapper;

    public InstructorController(InstructorService instructorService, InstructorMapper instructorMapper) {
        this.instructorService = instructorService;
        this.instructorMapper = instructorMapper;
    }

    @Operation(summary = "Create a new Instructor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor created successfully"),
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
    public InstructorDto create(@RequestBody CreateInstructorDto instructorDto) {
        Instructor instructor = instructorMapper.convertToEntity(instructorDto);
        return instructorMapper.convertToDto(instructorService.create(instructor));
    }

    @Operation(summary = "Update an existing Instructor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Instructor updated successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Instructor not found",
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
    public void update(@PathVariable Long id, @RequestBody InstructorDto instructorDto) {
        Instructor instructor = instructorMapper.convertToEntity(instructorDto);
        instructorService.update(id, instructor);
    }

    @Operation(summary = "Retrieve all Instructors or search by name/surname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Instructors retrieved successfully"),
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
    @GetMapping
    public List<InstructorDto> readAllOrSearch(
            @RequestParam Optional<String> name,
            @RequestParam Optional<String> surname,
            @RequestParam Optional<String> input) {

        if (Stream.of(name, surname, input).filter(Optional::isPresent).count() > 1) {
            throw new IllegalArgumentException("Only one search parameter (name, surname, or input) can be specified.");
        }

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Instructor retrieved successfully"),
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
    @GetMapping("/{id}")
    public InstructorDto readById(@PathVariable Long id) {
        Instructor instructor = instructorService.readById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found"));
        return instructorMapper.convertToDto(instructor);
    }

    @Operation(summary = "Delete an Instructor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Instructor deleted successfully"),
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
        instructorService.deleteById(id);
    }

    @Operation(summary = "Retrieve available Instructors for a given date, time, and optional ClassType")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of available Instructors retrieved successfully"),
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
    public List<Long> findAvailableInstructors(
            @RequestParam Optional<Long> classTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        return instructorService.findAvailableInstructors(classTypeId, date, time).stream()
                .map(Instructor::getId)
                .collect(Collectors.toList());
    }
}
