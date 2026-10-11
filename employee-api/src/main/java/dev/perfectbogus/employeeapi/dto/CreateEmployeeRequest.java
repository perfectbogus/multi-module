package dev.perfectbogus.employeeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request body for creating an employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {

    @Schema(description = "Employee full name", example = "Alice Garcia", minLength = 2, maxLength = 100)
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Schema(description = "Department name", example = "Engineering")
    @NotBlank
    private String department;

    @Schema(description = "Annual salary in USD", example = "90000.0", minimum = "0")
    @NotNull
    @Positive
    private Double salary;
}
