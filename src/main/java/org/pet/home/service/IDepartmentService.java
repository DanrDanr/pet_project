package org.pet.home.service;

import org.pet.home.common.DepartmentQuery;
import org.pet.home.entity.Department;
import org.pet.home.utils.Extype;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/27
 **/
public interface IDepartmentService {
    void add(Department d);
    void remove(Long id);
    void update(Department d);
    Department find(Long id);
    List<Department> findAll();
    Long queryCount();
    List<Department> findDepartmentsByPage(DepartmentQuery query);
    List<Department> getDepartmentTreeData();
    List< Extype > findTypes();
}
