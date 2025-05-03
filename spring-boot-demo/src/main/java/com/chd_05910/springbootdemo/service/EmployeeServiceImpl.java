package com.chd_05910.springbootdemo.service;

import com.chd_05910.springbootdemo.error.EmployeeNotFoundException;
import com.chd_05910.springbootdemo.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements  EmployeeService{

    List<Employee> employeeList = new ArrayList<>();

    @Override
    public Employee saveEmployee(Employee employee) {
        if ( employee.getEmpId() == null ) {
            employee.setEmpId(UUID.randomUUID().toString());
            employeeList.add(employee);
        }
        return employee;
    }

    @Override
    public List<Employee> getAllEmployee() {
        return employeeList;
    }

    @Override
    public Employee getEmpByID(String id) {
        return employeeList
                .stream()
                .filter(employee -> employee.getEmpId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException(" Employee Not Found With ID : " + id));
    }

    @Override
    public String deleteEmployee(String id) {
        Employee emp = employeeList
                .stream()
                .filter((e) -> e.getEmpId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException(" Employee Not Found With ID : " + id));
        employeeList.remove(emp);
        return "Employee is Deleted.";
    }
}
