package dev.perfectbogus.employeeapi.config;

import dev.perfectbogus.employeeapi.model.Employee;
import dev.perfectbogus.employeeapi.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final EmployeeRepository repository;
    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) return;
        repository.saveAll(employees());
    }

    private List<Employee> employees() {
        return List.of(
                Employee.builder().name("Ana García").department("Engineering").salary(85000.0).active(true).build(),
                Employee.builder().name("Carlos Hernández").department("Engineering").salary(92000.0).active(true).build(),
                Employee.builder().name("Lucía Martínez").department("Engineering").salary(78500.0).active(true).build(),
                Employee.builder().name("Jorge Ramírez").department("Engineering").salary(105000.0).active(false).build(),
                Employee.builder().name("María López").department("Human Resources").salary(56000.0).active(true).build(),
                Employee.builder().name("Diego Torres").department("Human Resources").salary(61000.0).active(true).build(),
                Employee.builder().name("Sofía Flores").department("Finance").salary(72000.0).active(true).build(),
                Employee.builder().name("Andrés Rivera").department("Finance").salary(88000.0).active(true).build(),
                Employee.builder().name("Valentina Cruz").department("Finance").salary(67500.0).active(false).build(),
                Employee.builder().name("Miguel Morales").department("Marketing").salary(59000.0).active(true).build(),
                Employee.builder().name("Camila Ortiz").department("Marketing").salary(63500.0).active(true).build(),
                Employee.builder().name("Fernando Reyes").department("Marketing").salary(70000.0).active(true).build(),
                Employee.builder().name("Isabella Jiménez").department("Sales").salary(54000.0).active(true).build(),
                Employee.builder().name("Ricardo Vargas").department("Sales").salary(58500.0).active(true).build(),
                Employee.builder().name("Daniela Castillo").department("Sales").salary(62000.0).active(false).build(),
                Employee.builder().name("Alejandro Romero").department("Operations").salary(66000.0).active(true).build(),
                Employee.builder().name("Paula Mendoza").department("Operations").salary(69500.0).active(true).build(),
                Employee.builder().name("Sebastián Navarro").department("IT Support").salary(48000.0).active(true).build(),
                Employee.builder().name("Gabriela Silva").department("IT Support").salary(51500.0).active(true).build(),
                Employee.builder().name("Emilio Guerrero").department("Legal").salary(95000.0).active(true).build()
        );
    }
}
