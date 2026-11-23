
package devtrack_api.dto;

import devtrack_api.enums.IssuePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IssueRequest {

    @NotBlank(message = "Issue title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Priority is required")
    private IssuePriority priority;

    public IssueRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }
}
