package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employees {

    private List<Employee> content;
    
    private Integer count;

    public void setContent(List<Employee> content) {
        this.content = content;
    }

    public List<Employee> getContent() {
        return content;
    }
}
