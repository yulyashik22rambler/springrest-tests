package com.example.test.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import com.example.model.Employee;

@Component
public class EmployeProvider {

    public static List<Employee> getEmployeesList(Integer count) {
        List<Employee> list = new ArrayList<Employee>();
        {
            for (int i = 0; i < count; i++) {
                String name = RandomStringUtils.randomAlphabetic(7);
                list.add(Employee.builder().id(Long.valueOf(i + 1)).firstName(name)
                        .lastName(RandomStringUtils.randomAlphabetic(10)).emailId(name + "@mail.ru").build());
            }
        }
        return list;
    }
}