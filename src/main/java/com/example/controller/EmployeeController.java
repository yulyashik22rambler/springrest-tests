package com.example.controller;

import java.util.Comparator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.handlers.BadRequestException;
import com.example.handlers.ErrorDetails;
import com.example.handlers.ResourceNotFoundException;
import com.example.model.Employee;
import com.example.model.EmployeeDeleted;
import com.example.model.Employees;
import com.example.repository.EmployeeRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * http://localhost:8080/swagger-ui.html#/
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")

@Api(value = "Employee Management System")

public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @ApiOperation(value = "View a list of all available employees")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list", response = Employees.class) })
    @GetMapping("/employees")
    public ResponseEntity<Employees> getAllEmployees() {
        List<Employee> listEmployees = employeeRepository.findAll();
        log.info("send GET /employees sucsessfuly");
        listEmployees.sort(Comparator.comparing(Employee::getFirstName));
        Employees employees = Employees.builder().content(listEmployees).count(listEmployees.size()).build();
        return new ResponseEntity<Employees>(employees, HttpStatus.OK);
    }

    @ApiOperation(value = "Get an employee by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Employee from db", response = Employee.class),
            @ApiResponse(code = 404, message = "Not Found - employee by id was not found", response = ErrorDetails.class) })
    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @ApiParam(value = "Employee id from which employee object will retrieve", required = true) @PathVariable(value = "id") Long employeeId)
            throws ResourceNotFoundException {
        log.info("send GET /employee/{id}, employeeId = " + employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));
        return ResponseEntity.ok().body(employee);

    }

    @ApiOperation(value = "Add an employee")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Employee from db", response = Employee.class),
            @ApiResponse(code = 400, message = "Bad request - parameters validation failed", response = ErrorDetails.class) })
    @PostMapping("/employee")
    public ResponseEntity<Employee> createEmployee(
            @ApiParam(value = "Employee for store in database table", required = true) @Valid @RequestBody Employee employee) {
        if (employee.getFirstName() == null) {
            throw new BadRequestException("Employee first name can not be empty");
        } else {
            return new ResponseEntity<Employee>(employeeRepository.save(employee), HttpStatus.OK);
        }

    }

    @ApiOperation(value = "Update an employee")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Updated employee", response = Employee.class),
            @ApiResponse(code = 400, message = "Bad request - parameters validation failed", response = ErrorDetails.class),
            @ApiResponse(code = 404, message = "Not Found - employee by id was not found", response = ErrorDetails.class), })
    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @ApiParam(value = "Employee Id to update employee object", required = true) @PathVariable(value = "id") Long employeeId,
            @ApiParam(value = "Update employee object", required = true) @Valid @RequestBody Employee employeeDetails)
            throws ResourceNotFoundException {
        if (employeeId == null) {
            throw new BadRequestException("Path parametr employeeId can not be empty");
        }
        // TODO
        if (employeeDetails.getFirstName() == null) {
            throw new BadRequestException("Employee first name can not be empty");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));

        employee.setEmailId(employeeDetails.getEmailId());
        employee.setLastName(employeeDetails.getLastName());
        employee.setFirstName(employeeDetails.getFirstName());
        final Employee updatedEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(updatedEmployee);

    }

    @ApiOperation(value = "Delete an employee")
    @DeleteMapping("/employees/{id}")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Updated employee", response = EmployeeDeleted.class),
            @ApiResponse(code = 400, message = "Bad request - parameters validation failed", response = ErrorDetails.class),
            @ApiResponse(code = 404, message = "Not Found - employee by id was not found", response = ErrorDetails.class), })
    public ResponseEntity<EmployeeDeleted> deleteEmployee(
            @ApiParam(value = "Employee Id from which employee object will delete from database table", required = true) @PathVariable(value = "id") Long employeeId)
            throws ResourceNotFoundException {
        if (employeeId == null) {
            throw new BadRequestException("Path parametr employeeId can not be empty");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));
        EmployeeDeleted employeeDeleted = EmployeeDeleted.builder().deleted(false).build();

        employeeRepository.delete(employee);
        employeeDeleted.setDeleted(true);

        return ResponseEntity.ok(employeeDeleted);

    }
}