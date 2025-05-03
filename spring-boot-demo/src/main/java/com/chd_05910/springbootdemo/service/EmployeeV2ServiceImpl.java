package com.chd_05910.springbootdemo.service;

import com.chd_05910.springbootdemo.entity.EmployeeEntity;
import com.chd_05910.springbootdemo.error.EmployeeNotFoundException;
import com.chd_05910.springbootdemo.model.Employee;
import com.chd_05910.springbootdemo.repository.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeV2ServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee saveEmployee(Employee employee) {
        if ( employee.getEmpId() == null ) {
            employee.setEmpId(UUID.randomUUID().toString());
        }

        EmployeeEntity entity = new EmployeeEntity();
        BeanUtils.copyProperties(employee,entity);
        employeeRepository.save(entity);
        return employee;
    }

    @Override
    public List<Employee> getAllEmployee() {
        List<EmployeeEntity> employeeEntityList = employeeRepository.findAll();
        List<Employee> employees = employeeEntityList
                .stream()
                .map(employeeEntity -> {
                    Employee employee = new Employee();
                    BeanUtils.copyProperties(employeeEntity,employee);
                    return employee;
                }).toList();
        return employees;
    }

    @Override
    public Employee getEmpByID(String id) {
        EmployeeEntity employeeEntity = employeeRepository.findById(id).get();
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeEntity,employee);
        return employee;
    }

    @Override
    public String deleteEmployee(String id) {
        employeeRepository.deleteById(id);
        return "Employee deleted with the id: " + id;
    }
}
