package dev.perfectbogus.employeeapi.controller;

import dev.perfectbogus.employeeapi.dto.CreateEmployeeRequest;
import dev.perfectbogus.employeeapi.dto.EmployeePatchRequest;
import dev.perfectbogus.employeeapi.dto.EmployeeResponse;
import dev.perfectbogus.employeeapi.dto.PageResponse;
import dev.perfectbogus.employeeapi.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody CreateEmployeeRequest request) {
        return service.create(request);
    }

    @GetMapping
    public PageResponse<EmployeeResponse> getAll(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "false") Boolean includeInactive,
            Pageable pageable
    ) {
        return service.filter(department, minSalary, maxSalary, includeInactive, pageable);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable Long id) {
        return service.getEmployeeResponseById(id);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody CreateEmployeeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}")
    public EmployeeResponse patch(@PathVariable Long id, @RequestBody EmployeePatchRequest request) {
        return service.patch(id, request);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<EmployeeResponse> restore(@PathVariable Long id) {
        return ResponseEntity.ok(service.restore(id));
    }
}
