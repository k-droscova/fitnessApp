package cz.cvut.fit.tjv.fitnessApp.controller.dto.classType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateClassTypeDto extends ClassTypeDto {
    @Override
    @Schema(hidden = true) // This hides the field in Swagger/OpenAPI schema
    public Long getId() {
        return super.getId();
    }

    @Override
    @Schema(hidden = true) // Prevents serialization/deserialization of `id`
    public void setId(Long id) {
        super.setId(id);
    }
}
