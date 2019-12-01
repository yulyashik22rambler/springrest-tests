package com.example.tests;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.Application;
import com.example.handlers.ErrorDetails;
import com.example.model.Employee;
import com.example.mybatis.EmployeesMapper;
import com.example.test.api.EmployeeApi;

import io.qameta.allure.Epic;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Epic("POST /employee")
public class PostEmployeeTest {

    @Autowired
    EmployeeApi employeeApi;

    @Autowired
    EmployeesMapper employeesMapper;

    static Stream<Arguments> postEmployeeData() {
        List<Arguments> testData = new ArrayList<>();
        {
            String description = "Call servise with all parameteres";
            Employee employee = Employee.builder().id(Long.valueOf(1)).firstName("Vadim").lastName("Ivanov")
                    .emailId("vivanov@mail.ru").build();
            Employee expectedEmployee = Employee.builder().id(employee.getId()).firstName(employee.getFirstName())
                    .lastName(employee.getLastName()).emailId(employee.getEmailId()).build();

            testData.add(Arguments.of(description, employee, expectedEmployee));
        }
        {
            String description = "Call servise with id, first name and last name";
            Employee employee = Employee.builder().id(Long.valueOf(2)).firstName("Nikodim").lastName("Jonson").build();
            Employee expectedEmployee = Employee.builder().id(employee.getId()).firstName(employee.getFirstName())
                    .lastName(employee.getLastName()).build();
            testData.add(Arguments.of(description, employee, expectedEmployee));
        }
        {
            String description = "Call servise with id and mandatory parameter ";
            Employee employee = Employee.builder().id(Long.valueOf(3)).firstName("Valdemar").build();
            Employee expectedEmployee = Employee.builder().id(employee.getId()).firstName(employee.getFirstName())
                    .build();
            testData.add(Arguments.of(description, employee, expectedEmployee));
        }
        {
            String description = "Call servise with only mandatory parameter";
            Employee employee = Employee.builder().firstName("Inokentiy").build();
            Employee expectedEmployee = Employee.builder().id(4).firstName(employee.getFirstName()).build();

            testData.add(Arguments.of(description, employee, expectedEmployee));
        }
        return testData.stream();
    }

    @DisplayName("POST /employee 200 Ok - Create new employee in DB")
    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("postEmployeeData")
    public void postEmployeeOkTest(String description, Employee employee, Employee expectedEmployee) {

        ResponseEntity<Employee> response = employeeApi.postNewEmployee(employee, Employee.class);
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Responce code is not 200");
        assertNotNull(response.getBody());
        assertThat("Method post employee return wrong data", response.getBody(), sameBeanAs(expectedEmployee));
    }

    @DisplayName("POST /employee 400 Bad Request - Try to create new employee without mandatory field - 'firstName'")
    @Test
    public void postEmployeeBadRequestTest() {
        Employee employee = Employee.builder().id(Long.valueOf(5)).build();
        ResponseEntity<ErrorDetails> response = employeeApi.postNewEmployee(employee, ErrorDetails.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Responce code is not 400");
        assertEquals("Employee first name can not be empty", response.getBody().getMessage());

    }
}
