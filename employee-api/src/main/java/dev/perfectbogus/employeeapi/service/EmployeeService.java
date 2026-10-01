package dev.perfectbogus.employeeapi.service;

import dev.perfectbogus.employeeapi.dto.CreateEmployeeRequest;
import dev.perfectbogus.employeeapi.dto.EmployeePatchRequest;
import dev.perfectbogus.employeeapi.dto.EmployeeResponse;
import dev.perfectbogus.employeeapi.dto.PageResponse;
import dev.perfectbogus.employeeapi.exception.EmployeeNotFoundException;
import dev.perfectbogus.employeeapi.mapper.EmployeeMapper;
import dev.perfectbogus.employeeapi.model.Employee;
import dev.perfectbogus.employeeapi.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Transactional(readOnly = true)
    public Employee getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public EmployeeResponse getEmployeeResponseById(Long id) {
        return mapper.toResponse(getById(id));
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        Employee saved = repository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = getById(id);
        employee.setActive(false);
        repository.save(employee);
    }

    @Transactional
    public EmployeeResponse update(Long id, CreateEmployeeRequest request) {
        Employee existing = getById(id);

        existing.setDepartment(request.getDepartment());
        existing.setName(request.getName());
        existing.setSalary(request.getSalary());

        Employee saved = repository.save(existing);

        return mapper.toResponse(saved);
    }

    @Transactional
    public EmployeeResponse patch(Long id, EmployeePatchRequest request) {
        Employee existing = getById(id);

        if (request.name() != null) existing.setName(request.name());
        if (request.department() != null) existing.setDepartment(request.department());
        if (request.salary() != null) existing.setSalary(request.salary());

        Employee saved = repository.save(existing);

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> filter(
            String department,
            Double minSalary,
            Double maxSalary,
            Boolean includeInactive,
            Pageable pageable) {
        boolean hasDept = department != null;
        boolean hasMin = minSalary != null;
        boolean hasMax = maxSalary != null;
        boolean activeOnly = !Boolean.TRUE.equals(includeInactive);

        double min = hasMin ? minSalary : 0.0;
        double max = hasMax ? maxSalary : Double.MAX_VALUE;

        Page<Employee> page;
        if (activeOnly) {
            if (!hasDept && !hasMin && !hasMax) {
                page = repository.findByActiveTrue(pageable);
            } else if (hasDept && !hasMin && !hasMax) {
                page = repository.findByActiveTrueAndDepartmentIgnoreCase(department, pageable);
            } else if (!hasDept) {
                page = repository.findByActiveTrueAndSalaryBetween(min, max, pageable);
            } else {
                page = repository.findByDepartmentIgnoreCaseAndSalaryBetween(department, min, max, pageable);
            }
        } else {
            if (!hasDept && !hasMin && !hasMax)
                page = repository.findAll(pageable);
            else if (hasDept && !hasMin && !hasMax)
                page = repository.findByDepartmentIgnoreCase(
                        department, pageable);
            else if (!hasDept)
                page = repository.findBySalaryBetween(
                        min, max, pageable);
            else
                page = repository.findByDepartmentIgnoreCaseAndSalaryBetween(
                        department, min, max, pageable);
        }

        return mapper.toResponsePage(page);
    }

}
