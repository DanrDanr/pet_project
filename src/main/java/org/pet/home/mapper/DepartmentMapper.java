package org.pet.home.mapper;

import org.apache.ibatis.annotations.*;
import org.pet.home.common.DepartmentQuery;
import org.pet.home.entity.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/26
 **/
@Mapper
@Repository
public interface DepartmentMapper {
    /**
     * 创建部门
     * @param d Department
     */
    @Insert("insert into t_department(sn,name,manager_id,parent_id,state)" +
            "values(#{sn},#{name},#{manager.id},#{parent.id},#{state})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    void add(Department d);

    @Delete("delete from t_department where id=#{id}")
    void remove(Long id);

    @Update("update t_department set " +
            "sn=#{sn},name=#{name},manager_id=#{manager.id}," +
            "parent_id=#{parent.id},state=#{state} " +
            "where id=#{id}")
    void update(Department d);



    @Select("select * from t_department where id=#{id}")
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.pet.home.mapper.DepartmentMapper.findParentDepartment")
            ),
            @Result(
                    property = "children",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "org.pet.home.mapper.DepartmentMapper.findSubDepartments")
            )
    })
    Department find(Long id);

    @Select("select * from t_department")
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parentId",
                    column = "parent_id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.pet.home.mapper.DepartmentMapper.findParentDepartment")
            ),
            @Result(
                    property = "children",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "org.pet.home.mapper.DepartmentMapper.findSubDepartments")
            )
    })
    List<Department> findAll();

    /**
     * 查询id的所有的子部门
     * @param id
     * @return
     */
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.pet.home.mapper.DepartmentMapper.findParentDepartment")
            ),

    })
    @Select("select * from t_department where parent_id=#{id}")
    List<Department> findSubDepartments(Long id);


    /**
     * 查询id = parent_ id的数据，用来查询id的 parent的组织
     * @param parent_id
     * @return
     */
    @Select("select * from t_department where id=#{parent_id}")
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.pet.home.mapper.DepartmentMapper.findParentDepartment")
            )
    })
    Department findParentDepartment(Long parent_id);


    @Select("select count(*) from t_department")
    Long queryCount();

    /**
     * 分页查询
     * @param query
     * @return
     */
    @Select("select " +
            "* from " +
            "t_department " +
            "limit #{start},#{pageSize}")
    @Results({
            @Result(
                    property = "id",
                    column = "id"
            ),
            @Result(
                    property = "parent",
                    column = "parent_id",
                    javaType = Department.class,
                    one = @One(select = "org.pet.home.mapper.DepartmentMapper.findParentDepartment")
            ),
            @Result(
                    property = "children",
                    column = "id",
                    javaType = List.class,
                    many = @Many(select = "org.pet.home.mapper.DepartmentMapper.findSubDepartments")
            )
    })
    List<Department> findDepartmentsByPage(DepartmentQuery query);
}
