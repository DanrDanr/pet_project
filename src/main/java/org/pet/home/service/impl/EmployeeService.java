package org.pet.home.service.impl;

import org.pet.home.entity.Department;
import org.pet.home.entity.Employee;
import org.pet.home.mapper.DepartmentMapper;
import org.pet.home.mapper.EmployeeMapper;
import org.pet.home.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/1
 **/
@Service
public class EmployeeService implements IEmployeeService {
    private EmployeeMapper employeeMapper;
    private DepartmentMapper departmentMapper;

    @Autowired
    public EmployeeService(EmployeeMapper employeeMapper,DepartmentMapper departmentMapper){
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public boolean add(Employee e) {
        int rows = employeeMapper.add(e);
        if(rows == 0){
            //添加失败
            return false;
        }
        else {
            Department department = departmentMapper.find(e.getDid());
            e.setDepartment(department);
            return true;
        }

    }

    @Override
    public Employee checkPhone(String phone) {
        return employeeMapper.checkPhone(phone);
    }

    @Override
    public List< Employee > list() {
        return employeeMapper.list();
    }

    @Override
    public void remove(Long id) {
        employeeMapper.remove(id);
    }

    @Override
    public void update(Employee e) {
       employeeMapper.update(e);
    }

    @Override
    public Employee login(String phone,String password) {
        return employeeMapper.login(phone, password);
    }

    @Override
    public Employee findById(Long id) {
        return employeeMapper.findById(id);
    }
}
