package dev.perfectbogus.employeeapi.exception;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException {

    private final Long id;

    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
        this.id = id;
    }

}
