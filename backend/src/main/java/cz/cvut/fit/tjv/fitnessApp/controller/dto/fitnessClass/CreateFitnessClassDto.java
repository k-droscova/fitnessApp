package cz.cvut.fit.tjv.fitnessApp.controller.dto.fitnessClass;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateFitnessClassDto extends FitnessClassDto {
    @Override
    @Schema(hidden = true) // Hides the `id` field in Swagger schema
    public Long getId() {
        return super.getId();
    }

    @Override
    @Schema(hidden = true) // Prevents deserialization of the `id` field
    public void setId(Long id) {
        super.setId(id);
    }
}
