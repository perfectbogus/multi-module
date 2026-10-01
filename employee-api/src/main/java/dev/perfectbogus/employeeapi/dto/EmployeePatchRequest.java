package dev.perfectbogus.employeeapi.dto;

public record EmployeePatchRequest(
        String name,
        String department,
        Double salary
) {}
