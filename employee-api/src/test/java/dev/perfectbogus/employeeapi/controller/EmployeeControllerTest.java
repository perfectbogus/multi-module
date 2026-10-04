package dev.perfectbogus.employeeapi.controller;

import dev.perfectbogus.employeeapi.dto.CreateEmployeeRequest;
import dev.perfectbogus.employeeapi.dto.EmployeePatchRequest;
import dev.perfectbogus.employeeapi.dto.EmployeeResponse;
import dev.perfectbogus.employeeapi.dto.EmployeeStats;
import dev.perfectbogus.employeeapi.exception.EmployeeNotFoundException;
import dev.perfectbogus.employeeapi.model.Employee;
import dev.perfectbogus.employeeapi.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    private final Long ID = 1L;
    private final String NAME = "Alice";
    private final String DEPT = "Engineering";
    private final double SALARY = 90000.0;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService service;

    @Autowired
    ObjectMapper objectMapper;

    private EmployeeResponse buildResponse(Long id, String name, String dept, double salary) {
        return new EmployeeResponse(id, name, dept, salary, true, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void getById_whenNotFound_returns200() throws Exception {
        // Given
        EmployeeResponse response = buildResponse(ID, NAME, DEPT, SALARY);
        when(service.getEmployeeResponseById(1L)).thenReturn(response);

        // When + Then
        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.department").value(DEPT))
                .andExpect(jsonPath("$.salary").value(SALARY))
                .andExpect(jsonPath("$.active").value(true));

    }

    @Test
    void getById_whenNotFound_returns400() throws Exception {
        final Long idNF = 999L;
        when(service.getEmployeeResponseById(idNF))
                .thenThrow(new EmployeeNotFoundException(idNF));

        mockMvc.perform(get("/employees/" + idNF))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Employee not found with id: " + idNF));
    }

    @Test
    void create_withValidRequest_returns201() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest(NAME, DEPT, SALARY);
        EmployeeResponse response = buildResponse(ID, NAME, DEPT, SALARY);

        when(service.create(any(CreateEmployeeRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.name").value(NAME));
    }

    @Test
    void create_withBlankName_returns400() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest("", DEPT, SALARY);

        mockMvc.perform(post("/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void delete_validId_return204() throws Exception {
        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_name_returns200() throws Exception {
        final String newName = "Ana";
        EmployeeResponse response = buildResponse(ID, newName, DEPT, SALARY);
        CreateEmployeeRequest request = new CreateEmployeeRequest(newName, DEPT, SALARY);

        when(service.update(ID, request)).thenReturn(response);

        mockMvc.perform(put("/employees/" + ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void patch_name_return200() throws Exception {
        final String newName = "Ana";
        EmployeePatchRequest request = new EmployeePatchRequest(newName, DEPT, SALARY);
        EmployeeResponse response = buildResponse(ID, newName, DEPT, SALARY);

        when(service.patch(ID, request)).thenReturn(response);

        mockMvc.perform(patch("/employees/" + ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk());
    }

    @Test
    void stats_return200() throws Exception {
        Map<String, Long> employeeCountByDept = Map.of(DEPT, 1L);
        Map<String, Double> avgSalaryByDept = Map.of(DEPT, 90000.0);
        EmployeeStats stats =
                new EmployeeStats(1, 1, 0, SALARY, SALARY, SALARY,
                        employeeCountByDept, avgSalaryByDept);

        when(service.getStats()).thenReturn(stats);

        mockMvc.perform(get("/employees/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(1))
                .andExpect(jsonPath("$.activeEmployees").value(1))
                .andExpect(jsonPath("$.inactiveEmployees").value(0))
                .andExpect(jsonPath("$.averageSalary").value(SALARY))
                .andExpect(jsonPath("$.employeeCountByDepartment." + DEPT).value(1));



    }

    @Test
    void getEmployeeById_invalidId_throwEmployeeNotFound() throws Exception {
        when(service.getEmployeeResponseById(999L))
                .thenThrow(new EmployeeNotFoundException(999L));

        mockMvc.perform(get("/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 999"));
    }

    @Test
    void delete_invalidId_throwsEmployeeNotFoundException() throws Exception {
        doThrow(new EmployeeNotFoundException(999L))
                .when(service).delete(999L);

        mockMvc.perform(delete("/employees/999"))
                .andExpect(status().isNotFound());
    }
}