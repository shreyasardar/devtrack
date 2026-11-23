
package devtrack_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LabelRequest {

    @NotBlank(message = "Label name cannot be empty")
    @Size(max = 50, message = "Label name cannot exceed 50 characters")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @Pattern(
        regexp = "^#[0-9a-fA-F]{6}$",
        message = "Color must be a valid hex color, e.g. #FF5733"
    )
    private String color;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
