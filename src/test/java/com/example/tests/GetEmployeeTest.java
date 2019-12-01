package com.example.tests;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.Application;
import com.example.handlers.ErrorDetails;
import com.example.model.Employee;
import com.example.model.Employees;
import com.example.mybatis.EmployeesMapper;
import com.example.test.api.EmployeeApi;
import com.example.test.provider.EmployeProvider;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Epic("GET /employee")
public class GetEmployeeTest {

    @Autowired
    EmployeeApi employeeApi;

    @Autowired
    EmployeesMapper employeesMapper;

    @BeforeEach
    public void cleanDbBefore() {
        employeesMapper.deleteAll();
    }

    @AfterEach
    public void cleanDb() {
        employeesMapper.deleteAll();
    }

    @Description("Get no eployees from DB")
    @Test
    public void getEmployeesEmpty200Test() {
        ResponseEntity<Employees> response = employeeApi.getAllEmployees(Employees.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "State code is not 200");
        assertEquals(0, response.getBody().getCount());
        assertNull(response.getBody().getContent());
    }

    @Description("Get all eployees from DB")
    @ParameterizedTest(name = "Count of employees in db = {0}")
    @ValueSource(ints = { 1, 20, 100 })
    public void getEmployees200Test(int employeesCount) {

        List<Employee> employesInDB = EmployeProvider.getEmployeesList(employeesCount);
        Employees expectedEemployees = Employees.builder().content(employesInDB).count(employeesCount).build();

        employesInDB.forEach(employeesMapper::insert);

        ResponseEntity<Employees> response = employeeApi.getAllEmployees(Employees.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "State code is not 200");
        assertEquals(expectedEemployees.getCount(), response.getBody().getCount());

        expectedEemployees.getContent().sort(Comparator.comparing(Employee::getFirstName));
        assertThat("Method get employees return wrong content or sorted not first name order",
                response.getBody().getContent(), sameBeanAs(expectedEemployees.getContent()));
    }

    @Description("200 Ok Get eployee by id from DB")
    @Test
    public void getEmployeeById200Test() {
        int employeesCount = 10;
        List<Employee> employesInDB = EmployeProvider.getEmployeesList(employeesCount);
        employesInDB.forEach(employeesMapper::insert);

        employesInDB.forEach(employee -> {
            final ResponseEntity<Employee> response = employeeApi.getEmployeeById(Long.valueOf(employee.getId()),
                    Employee.class);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "State code is not 200");
            assertThat("In response should be write employee", response.getBody(), sameBeanAs(employee));
        });
    }

    @Description("404 Not Found Try to get eployee by id from DB")
    @Test
    public void getEmployeeById404Test() {
        Long employeeId = Long.valueOf(1500);
        List<Employee> employesInDB = EmployeProvider.getEmployeesList(2);
        employesInDB.forEach(employeesMapper::insert);

        ResponseEntity<ErrorDetails> response = employeeApi.getEmployeeById(employeeId, ErrorDetails.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "State code is not 200");
        assertEquals("Employee not found for this id :: " + employeeId, response.getBody().getMessage());
    }

}
