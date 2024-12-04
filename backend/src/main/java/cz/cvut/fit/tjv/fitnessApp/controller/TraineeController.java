package cz.cvut.fit.tjv.fitnessApp.controller;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.trainee.CreateTraineeDto;
import cz.cvut.fit.tjv.fitnessApp.controller.dto.trainee.TraineeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.errorHandling.ErrorResponseImpl;
import cz.cvut.fit.tjv.fitnessApp.service.TraineeService;
import cz.cvut.fit.tjv.fitnessApp.service.mappers.TraineeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "/trainee", produces = MediaType.APPLICATION_JSON_VALUE)
public class TraineeController {

    private final TraineeService traineeService;
    private final TraineeMapper traineeMapper;

    public TraineeController(TraineeService traineeService, TraineeMapper traineeMapper) {
        this.traineeService = traineeService;
        this.traineeMapper = traineeMapper;
    }

    @Operation(summary = "Create a new Trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee created successfully"),
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
    public TraineeDto create(@RequestBody CreateTraineeDto traineeDto) {
        Trainee trainee = traineeMapper.convertToEntity(traineeDto);
        return traineeMapper.convertToDto(traineeService.create(trainee));
    }

    @Operation(summary = "Update an existing Trainee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee updated successfully"),
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
    public void update(@PathVariable Long id, @RequestBody TraineeDto traineeDto) {
        Trainee trainee = traineeMapper.convertToEntity(traineeDto);
        traineeService.update(id, trainee);
    }

    @Operation(summary = "Retrieve all Trainees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Trainees retrieved successfully"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
    })
    @GetMapping
    public List<TraineeDto> readAll() {
        List<Trainee> trainees = traineeService.readAll();
        return traineeMapper.convertManyToDto(trainees);
    }

    @Operation(summary = "Retrieve a Trainee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request (e.g., Trainee not found)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseImpl.class))
            )
    })
    @GetMapping("/{id}")
    public TraineeDto readById(@PathVariable Long id) {
        Trainee trainee = traineeService.readById(id).orElseThrow(() ->
                new IllegalArgumentException("Trainee not found for ID: " + id));
        return traineeMapper.convertToDto(trainee);
    }

    @Operation(summary = "Delete a Trainee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee deleted successfully"),
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
        traineeService.deleteById(id);
    }

    @Operation(summary = "Retrieve all Trainees by FitnessClass ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Trainees retrieved successfully"),
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
    @GetMapping("/fitness-class/{fitnessClassId}")
    public List<TraineeDto> findByFitnessClassId(@PathVariable Long fitnessClassId) {
        List<Trainee> trainees = traineeService.findTraineesByFitnessClassId(fitnessClassId);
        return traineeMapper.convertManyToDto(trainees);
    }
}
