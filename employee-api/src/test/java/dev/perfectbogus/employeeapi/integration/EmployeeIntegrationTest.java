package dev.perfectbogus.employeeapi.integration;

import dev.perfectbogus.employeeapi.dto.CreateEmployeeRequest;
import dev.perfectbogus.employeeapi.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
