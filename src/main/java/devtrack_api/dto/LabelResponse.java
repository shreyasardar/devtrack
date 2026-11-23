
package devtrack_api.dto;

public class LabelResponse {

    private Long id;
    private String name;
    private String description;
    private String color;
    private Long projectId;

    public LabelResponse(
            Long id,
            String name,
            String description,
            String color,
            Long projectId) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }

    public Long getProjectId() {
        return projectId;
    }
}
