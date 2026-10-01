package dev.perfectbogus.employeeapi.dto;

public record EmployeeResponse(
        Long id,
        String name,
        String department,
        Double salary
) {

}
