package dev.perfectbogus.employeeapi.repository;

import dev.perfectbogus.employeeapi.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByDepartmentIgnoreCase(String department, Pageable pageable);
    Page<Employee> findBySalaryBetween(Double min, Double max,Pageable pageable);
    Page<Employee> findByDepartmentIgnoreCaseAndSalaryBetween(String dept, Double min, Double max, Pageable pageable);
}
