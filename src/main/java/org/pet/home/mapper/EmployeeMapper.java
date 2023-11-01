package org.pet.home.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.pet.home.entity.Employee;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/1
 **/
@Mapper
@Repository
public interface EmployeeMapper {
    /**
     * 添加员工
     * @param e 员工信息
     */

    @Insert("insert into t_employee(username,email,phone,password,age,state,did)" +
            "values(#{username},#{email},#{phone},#{password},#{age},#{state},#{did})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(Employee e);
}
