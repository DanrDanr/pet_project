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

    @Select("SELECT * FROM petCommodity where id=#{id}")
    PetCommodity check(Long id);

    @Select("SELECT * FROM petCommodity where state=#{state} and employee_id=#{employee_id}")
    List< PetCommodity > findByState(int state, long employee_id);

    @Select("SELECT * FROM petCommodity where shop_id=#{shop_id}")
    List< PetCommodity > findByShop(long shop_id);

    @Update("update petCommodity set state=#{state} where id=#{id}")
    int updateState(int state,long id);

    /**
     * 领养宠物 商品下架
     * @param
     * @param id
     * @return
     */
    @Update("update petCommodity set adopt=1, user_id=#{user_id}, endTime=#{endTime},state=0 where id=#{id}")
    int petAdopt(long user_id,long endTime,long id);

    /**
     * 查看用户领养名单
     */
    @Select("SELECT * FROM petCommodity where user_id=#{user_id} and adopt=1")
    List< PetCommodity > findByUser(long user_id);
}
