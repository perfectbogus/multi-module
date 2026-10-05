package dev.perfectbogus.employeeapi.integration;

import dev.perfectbogus.employeeapi.dto.CreateEmployeeRequest;
import dev.perfectbogus.employeeapi.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    EmployeeRepository repository;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    private CreateEmployeeRequest buildRequest(String name, String dept, double salary) {
        return new CreateEmployeeRequest(name, dept, salary);
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private Long createEmployee(String name, String dept, double salary) throws Exception {
        MvcResult result = mockMvc.perform(post("/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(buildRequest(name, dept, salary))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void create_validEmployee_savedInDatabase() throws Exception {
        mockMvc.perform(post("/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(buildRequest("Alice", "Engineering", 90000))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists());

        // Verify actually saved in DB
        assertEquals(1, repository.count());
    }

    @Test
    void create_blankName_returns400() throws Exception {
        mockMvc.perform(post("/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(buildRequest("", "Engineering", 90000))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").exists());

        assertEquals(0, repository.count());
    }

    @Test
    void create_negativeSalary_returns400() throws Exception {
        mockMvc.perform(post("/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(buildRequest("Alice", "Engineering", -80000))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

        assertEquals(0, repository.count());
    }

    @Test
    void getById_existingEmployee_returns200() throws Exception {
        Long id = createEmployee("Alice", "Engineering", 90000);
        mockMvc.perform(get("/employees/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void getById_nonExistent_return404() throws Exception {
        mockMvc.perform(get("/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void delete_softDeletesEmployee() throws Exception {
        Long id = createEmployee("Alice", "Engineering", 90000);

        mockMvc.perform(delete("/employees/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        mockMvc.perform(get("/employees?includeInactive=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].active").value(false));

        assertEquals(1, repository.count());

        mockMvc.perform(get("/employees/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void restore_softDeletedEmployee_reactivates() throws Exception {
        Long id = createEmployee("Alice", "Engineering", 90000);

        mockMvc.perform(delete("/employees/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/employees/" + id + "/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void update_existingEmployee_replacesAllFields() throws Exception {
        Long id = createEmployee("Alice", "Engineering", 90000);

        CreateEmployeeRequest update = buildRequest("Alice Updated", "HR", 95000);

        mockMvc.perform(put("/employees/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"))
                .andExpect(jsonPath("$.department").value("HR"))
                .andExpect(jsonPath("$.salary").value(95000.0));
    }

    @Test
    void getAll_withPagination_returnCorrectMetadata() throws Exception {
        for (int i = 1; i <= 15; i++) {
            createEmployee("Employee " + i, "Engineering", 50000 + i * 1000);
        }

        mockMvc.perform(get("/employees?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/employees?page=2&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getAll_filterByDpt_returnsOnlyMatching() throws Exception {
        createEmployee("Alice", "Engineering", 90000);
        createEmployee("Bob", "Marketing", 70000);
        createEmployee("Carol", "Engineering", 85000);

        mockMvc.perform(get("/employees?department=Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].department").value("Engineering"));
    }

}
