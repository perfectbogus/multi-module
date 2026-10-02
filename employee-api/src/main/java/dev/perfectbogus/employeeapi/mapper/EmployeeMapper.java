package dev.perfectbogus.employeeapi.mapper;

import dev.perfectbogus.employeeapi.dto.CreateEmployeeRequest;
import dev.perfectbogus.employeeapi.dto.EmployeeResponse;
import dev.perfectbogus.employeeapi.dto.PageResponse;
import dev.perfectbogus.employeeapi.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeMapper {
    public Employee toEntity(CreateEmployeeRequest request){
        return Employee.builder()
                .name(request.getName())
                .department(request.getDepartment())
                .salary(request.getSalary())
                .active(true)
                .build();
    }

    public EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.isActive(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }

    public List<EmployeeResponse> toResponseList(List<Employee> employees) {
        return employees.stream()
                .map(this::toResponse)
                .toList();
    }

    public PageResponse<EmployeeResponse> toResponsePage(Page<Employee> page) {
        return new PageResponse<>(
                toResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
