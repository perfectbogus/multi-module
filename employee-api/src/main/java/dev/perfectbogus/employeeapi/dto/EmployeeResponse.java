package dev.perfectbogus.employeeapi.dto;

import java.time.LocalDateTime;

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
