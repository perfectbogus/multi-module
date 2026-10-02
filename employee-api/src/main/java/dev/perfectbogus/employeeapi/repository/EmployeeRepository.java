package dev.perfectbogus.employeeapi.repository;

import dev.perfectbogus.employeeapi.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByDepartmentIgnoreCase(String department, Pageable pageable);
    Page<Employee> findBySalaryBetween(Double min, Double max,Pageable pageable);
    Page<Employee> findByDepartmentIgnoreCaseAndSalaryBetween(String dept, Double min, Double max, Pageable pageable);
    Page<Employee> findByActiveTrue(Pageable pageable);
    Page<Employee> findByActiveTrueAndDepartmentIgnoreCase(String dept, Pageable pageable);
    Page<Employee> findByActiveTrueAndSalaryBetween(Double min, Double max, Pageable pageable);
    Page<Employee> findByActiveTrueAndDepartmentIgnoreCaseAndSalaryBetween(
            String dept, Double min, Double max, Pageable pageable);
    Optional<Employee> findByIdAndActiveTrue(Long id);
    long countByActiveTrue();
    List<Employee> findByActiveTrue();
}
