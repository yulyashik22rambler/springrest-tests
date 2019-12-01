package com.example.test.api;

import java.util.Collections;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.model.Employee;

@Service
public class EmployeeApi {
    @Autowired
    Environment environment;

    @Autowired
    RestTemplate restTemplate;

    public String getBaseUrl() {
        return String.format("http://localhost:%d/api/v1/",
                environment.getProperty("local.server.port", Integer.class));
    }

    private String getEmployeesUrl = "employees";
    private String postEmployeeUrl = "employee";
    private String getEmployeeByIdUrl = "employee/{id}";

    public <T> ResponseEntity<T> getAllEmployees(Class<T> clazz) {
        return restTemplate.getForEntity(getBaseUrl() + getEmployeesUrl, clazz);
    }

    public <T> ResponseEntity<T> postNewEmployee(Employee employee, Class<T> clazz) {      
        return sendRequest(getBaseUrl() + postEmployeeUrl, clazz, employee, null, HttpMethod.POST);
    }

    public <T> ResponseEntity<T> getEmployeeById(Long id, Class<T> clazz) {
        return restTemplate.getForEntity(getBaseUrl() + getEmployeeByIdUrl.replace("{id}", id.toString()), clazz);

    }

    protected <T> ResponseEntity<T> sendRequest(String url, Class<T> clazz, Object request, 
            HashMap<String, ?> parameters, HttpMethod method) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> requestEntity = new HttpEntity<>(request, header);
//TODO Add parameters
        return restTemplate.exchange(url, method, requestEntity, clazz);
    }
}
