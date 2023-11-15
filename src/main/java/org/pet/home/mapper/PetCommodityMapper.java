package org.pet.home.mapper;

import org.apache.ibatis.annotations.*;
import org.pet.home.entity.PetCommodity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/15
 **/
@Mapper
@Repository
public interface PetCommodityMapper {
    /**
     * 添加宠物类别
     * @param petCommodity
     * @return
     */
    @Insert("insert into petCommodity(petName,sex,birth,costPrice,sellPrice,isInoculation,sellTime," +
            "shop_id,employee_id,petCategory_id,user_id,petFindMaster_id)" +
            "values(#{petName},#{sex},#{birth},#{costPrice},#{sellPrice},#{isInoculation},#{sellTime}," +
            "#{shop_id},#{employee_id},#{petCategory_id},#{user_id},#{petFindMaster_id})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(PetCommodity petCommodity);

    @Select("SELECT * FROM petCommodity where petFindMaster_id=#{id}")
    PetCommodity findByPetFindMaster_id(Long id);

    @Select("SELECT * FROM petCommodity where state=#{state} and employee_id=#{employee_id}")
    List< PetCommodity > findByState(int state, long employee_id);

    @Update("update petCommodity set state=#{state} where id=#{id}")
    int updateState(int state,long id);
}
