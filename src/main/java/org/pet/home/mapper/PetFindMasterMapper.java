package org.pet.home.mapper;

import org.apache.ibatis.annotations.*;
import org.pet.home.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    @Insert("insert into petFindMaster(petName,sex,address,birth,price,isInoculation,createTime,shop_id,employee_id,petCategory_id,user_id)" +
            "values(#{petFindMaster.petName},#{petFindMaster.sex},#{petFindMaster.address},#{petFindMaster.birth},#{petFindMaster.price}," +
            "#{petFindMaster.isInoculation},#{petFindMaster.createTime},#{shop.id},#{employee.id},#{petCategory.id},#{user.id})")
    @Options(useGeneratedKeys = true, keyProperty = "petFindMaster.id")
    int add(Shop shop, Employee employee, PetCategory petCategory,User user,PetFindMaster petFindMaster);

    /**
     * 根据id寻找寻主任务
     * @param id
     * @return
     */
    @Select("SELECT * FROM petFindMaster where id=#{id}")
    PetFindMaster findById(Long id);

    /**
     * 根据寻主任务状态查询 0是待处理 1是已处理
     * @param state
     * @return
     */
    @Select("SELECT * FROM petFindMaster where state=#{state}")
    List<PetFindMaster> findByState(int state);

    /**
     * 修改寻主任务状态
     * @param state
     * @return
     */
    @Update("update petFindMaster set state=#{state} where id=#{id}")
    void updateState(int state,long id);
}
