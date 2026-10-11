package dev.perfectbogus.employeeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Employee data returned by the API")
public record EmployeeResponse(
        Long id,
        String name,
        String department,
        Double salary,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
