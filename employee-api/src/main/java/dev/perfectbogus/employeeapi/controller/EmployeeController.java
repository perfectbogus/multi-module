package dev.perfectbogus.employeeapi.controller;

import dev.perfectbogus.employeeapi.dto.*;
import dev.perfectbogus.employeeapi.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Employees", description = "Employee management operations")
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService service;

    @Operation(summary = "Get employee statistics")
    @ApiResponse(responseCode = "200", description = "Statistics returned")
    @GetMapping("/stats")
    public EmployeeStats getStats() {
        return service.getStats();
    }

    @Operation(summary = "Create employee")
    @ApiResponse(responseCode = "201", description = "Employee created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody CreateEmployeeRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Get all employees", description = "Return paginated employees with optional filters")
    @ApiResponse(responseCode = "200", description = "Success")
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

    @Operation(summary = "Get employee by ID")
    @ApiResponse(responseCode = "200", description = "Employee found")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable Long id) {
        return service.getEmployeeResponseById(id);
    }

    @Operation(summary = "Replace employee (full update)")
    @ApiResponse(responseCode = "200", description = "Employee updated")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody CreateEmployeeRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Soft delete employee")
    @ApiResponse(responseCode = "204", description = "Employee deleted")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(summary = "Partial update employee")
    @ApiResponse(responseCode = "200", description = "Employee updated")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @PatchMapping("/{id}")
    public EmployeeResponse patch(@PathVariable Long id, @RequestBody EmployeePatchRequest request) {
        return service.patch(id, request);
    }

    @Operation(summary = "Restore soft-deleted employee")
    @ApiResponse(responseCode = "200", description = "Employee restored")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    @PutMapping("/{id}/restore")
    public ResponseEntity<EmployeeResponse> restore(@PathVariable Long id) {
        return ResponseEntity.ok(service.restore(id));
    }
}
