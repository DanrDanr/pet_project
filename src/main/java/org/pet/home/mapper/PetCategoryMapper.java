package org.pet.home.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.pet.home.entity.PetCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@Mapper
@Repository
public interface PetCategoryMapper {
    /**
     * 添加宠物类别
     * @param petCategory
     * @return
     */
    @Insert("insert into PetCategory(petType,description)" +
            "values(#{petType},#{description})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(PetCategory petCategory);

    /**
     * 查询所有宠物类别
     * @return
     */
    @Select("SELECT * FROM PetCategory")
    List<PetCategory> list();

    @Select("SELECT * FROM PetCategory where id=#{id}")
    PetCategory findById(Long id);
}
