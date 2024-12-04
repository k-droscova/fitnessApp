package cz.cvut.fit.tjv.fitnessApp.errorHandling;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Error Response")
public class ErrorResponseImpl implements ErrorResponse {
    @Schema(description = "Detailed error message", example = "Error message")
    private String errorMessage;
}