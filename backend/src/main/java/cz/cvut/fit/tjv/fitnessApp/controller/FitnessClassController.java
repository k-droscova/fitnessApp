package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.fitnessClass.CreateFitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.fitnessClass.FitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.errorHandling.ErrorResponseImpl;
import cz.cvut.fit.tjv.fitnessApp.service.FitnessClassService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.FitnessClassMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/fitness-class", produces = MediaType.APPLICATION_JSON_VALUE)
public class FitnessClassController {

    private final FitnessClassService fitnessClassService;
    private final FitnessClassMapper fitnessClassMapper;

    public FitnessClassController(FitnessClassService fitnessClassService, FitnessClassMapper fitnessClassMapper) {
        this.fitnessClassService = fitnessClassService;
        this.fitnessClassMapper = fitnessClassMapper;
    }

    @Operation(summary = "Create and schedule a new FitnessClass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FitnessClass created and scheduled successfully"),
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
    public FitnessClassDto create(@RequestBody CreateFitnessClassDto fitnessClassDto) {
        FitnessClass fitnessClass = fitnessClassMapper.convertToEntity(fitnessClassDto);
        fitnessClassService.scheduleClass(fitnessClass); // Use scheduleClass for validation and creation
        return fitnessClassMapper.convertToDto(fitnessClass);
    }

    @Operation(summary = "Update an existing FitnessClass by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "FitnessClass updated successfully"),
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
    public void update(@PathVariable Long id, @RequestBody FitnessClassDto fitnessClassDto) {
        FitnessClass fitnessClass = fitnessClassMapper.convertToEntity(fitnessClassDto);
        fitnessClassService.validateAndUpdate(id, fitnessClass);
    }

    @Operation(summary = "Retrieve all FitnessClasses or filter by date, time range, or room")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of FitnessClasses retrieved"),
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
    public List<FitnessClassDto> readAllWithFilters(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) Long roomId) {

        if (date != null && startTime != null && endTime != null && roomId != null) {
            throw new IllegalArgumentException("Cannot filter by date, time range, and room simultaneously. Please refine your query.");
        }

        List<FitnessClass> fitnessClasses;
        if (date != null && startTime != null && endTime != null) {
            fitnessClasses = fitnessClassService.readAllByDateAndTimeBetween(date, startTime, endTime);
        } else if (date != null && roomId != null) {
            fitnessClasses = fitnessClassService.readAllByDateAndRoomId(date, roomId);
        } else if (date != null) {
            fitnessClasses = fitnessClassService.readAllByDate(date);
        } else {
            fitnessClasses = fitnessClassService.readAll();
        }

        return fitnessClassMapper.convertManyToDto(fitnessClasses);
    }

    @Operation(summary = "Retrieve a FitnessClass by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FitnessClass retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request (e.g., FitnessClass not found)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
    })
    @GetMapping("/{id}")
    public FitnessClassDto readById(@PathVariable Long id) {
        FitnessClass fitnessClass = fitnessClassService.readById(id).orElseThrow(() ->
                new IllegalArgumentException("FitnessClass not found for ID: " + id));
        return fitnessClassMapper.convertToDto(fitnessClass);
    }

    @Operation(summary = "Delete a FitnessClass by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "FitnessClass deleted successfully"),
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
        fitnessClassService.deleteById(id);
    }

    @Operation(summary = "Add a Trainee to a FitnessClass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee added to FitnessClass successfully"),
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
    @PostMapping("/{id}/add-trainee/{traineeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addTraineeToClass(@PathVariable Long id, @PathVariable Long traineeId) {
        fitnessClassService.addTraineeToClass(id, traineeId);
    }

    @Operation(summary = "Remove a Trainee from a FitnessClass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee removed from FitnessClass successfully"),
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
    @PostMapping("/{id}/remove-trainee/{traineeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeTraineeFromClass(@PathVariable Long id, @PathVariable Long traineeId) {
        fitnessClassService.deleteTraineeFromClass(id, traineeId);
    }

    @Operation(summary = "Retrieve all trainees enrolled in a FitnessClass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of trainees retrieved successfully"),
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
    @GetMapping("/{id}/trainees")
    public List<Long> getTraineesByFitnessClass(@PathVariable Long id) {
        return fitnessClassService.findTraineesById(id).stream()
                .map(Trainee::getId)
                .collect(Collectors.toList());
    }
}
