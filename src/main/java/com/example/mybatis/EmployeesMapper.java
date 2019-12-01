package com.example.mybatis;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.example.model.Employee;

@Component
@Mapper
public interface EmployeesMapper {

    @Select("SELECT * FROM EMPLOYEES WHERE id = #{id}")
    @Results(value = { @Result(property = "id", column = "id"), 
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "emailId", column = "email_address") })
    Employee getEmployeById(@Param("id") Long id);

    @Insert("INSERT INTO EMPLOYEES (id,first_name,last_name,email_address) "
            + "VALUES (#{employee.id},#{employee.firstName}, #{employee.lastName}, #{employee.emailId})")
    public Long insert(@Param("employee") Employee employee);

    @Delete("DELETE FROM EMPLOYEES WHERE id = #{id}")
    public void deleteEmployeById(@Param("id") Long id);

    @Delete("DELETE FROM EMPLOYEES")
    public void deleteAll();
}
