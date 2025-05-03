package com.chd_05910.springbootdemo.service;

import com.chd_05910.springbootdemo.model.Employee;

import java.util.List;

public interface EmployeeService {
    public Employee saveEmployee(Employee employee);

    public List<Employee> getAllEmployee();

    public Employee getEmpByID(String id);

    public String deleteEmployee(String id);
}
