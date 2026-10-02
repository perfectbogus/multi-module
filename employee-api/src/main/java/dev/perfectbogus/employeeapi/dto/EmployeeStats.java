package dev.perfectbogus.employeeapi.dto;

import java.util.Map;

public record EmployeeStats(
        long totalEmployees,
        long activeEmployees,
        long inactiveEmployees,
        double averageSalary,
        double highestSalary,
        double lowestSalary,
        Map<String, Long> employeeCountByDepartment,
        Map<String, Double> averageSalaryByDepartment
) {
}
