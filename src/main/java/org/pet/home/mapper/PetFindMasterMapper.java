package org.pet.home.mapper;

import org.apache.ibatis.annotations.*;
import org.pet.home.entity.*;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/11
 **/
@Mapper
@Repository
public interface PetFindMasterMapper {
    /**
     * 添加寻主任务
     * @param petFindMaster
     * @return
     */
    @Insert("insert into petFindMaster(petName,sex,address,birth,price,isInoculation,createTime)" +
            "values(#{petName},#{sex},#{address},#{birth},#{price},#{isInoculation},#{createTime})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(PetFindMaster petFindMaster);

    @Update("update petFindMaster set shop_id=#{shop.id},employee_id=#{employee.id},petCategory_id=#{petCategory.id},user_id=#{user.id} " +
            "where id=#{petFindMaster.id}")
    int addTask(@Param("shop") Shop shop, @Param("employee") Employee employee
    , @Param("petCategory")PetCategory petCategory, @Param("user")User user,@Param("petFindMaster")PetFindMaster petFindMaster);
}
