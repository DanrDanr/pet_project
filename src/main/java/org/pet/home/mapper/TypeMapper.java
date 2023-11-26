package org.pet.home.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.pet.home.entity.Type;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/26
 **/
@Mapper
@Repository
public interface TypeMapper {
    @Select("SELECT * FROM type WHERE id = #{id}")
    Type findById(long id);
}
