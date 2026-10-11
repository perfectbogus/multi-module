package dev.perfectbogus.employeeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Employee data returned by the API")
public record EmployeeResponse(
        @Schema(description = "Unique identifier", example = "1")
        Long id,

        @Schema(description = "Employee full name", example = "Alice Garcia")
        String name,

        @Schema(description = "Department", example = "Engineering")
        String department,

        @Schema(description = "Annual salary", example = "90000.0")
        Double salary,

        @Schema(description = "Whether employee is active")
        boolean active,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {

}
