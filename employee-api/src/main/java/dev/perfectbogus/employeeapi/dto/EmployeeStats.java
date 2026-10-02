package dev.perfectbogus.employeeapi.dto;

import java.util.Map;

public record EmployeeStats(
        long totalEmployees,
        long activeEmployee,
        long inactiveEmployee,
        double averageSalary,
        double highestSalary,
        double lowestSalary,
        Map<String, Long> employeeCountByDepartment,
        Map<String, Double> averageSalaryByDepartment
) {
}
