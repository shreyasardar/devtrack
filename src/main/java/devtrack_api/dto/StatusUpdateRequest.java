package devtrack_api.dto;

import devtrack_api.enums.IssueStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private IssueStatus status;

    public StatusUpdateRequest() {
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }
}