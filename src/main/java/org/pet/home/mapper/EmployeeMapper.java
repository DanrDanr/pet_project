package org.pet.home.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.pet.home.entity.Employee;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 查询某号码是否被使用
     * @param phone
     * @return
     */
    @Select("SELECT * FROM t_employee WHERE phone = #{phone}")
    Employee checkPhone(String phone);

    /**
     * 查询所有员工
     * @return
     */
    @Select("SELECT * FROM t_employee")
    List<Employee> list();
}
