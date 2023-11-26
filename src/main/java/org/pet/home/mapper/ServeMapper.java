package org.pet.home.mapper;

import org.apache.ibatis.annotations.*;
import org.pet.home.entity.Serve;
import org.pet.home.entity.Shop;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/26
 **/
@Mapper
@Repository
public interface ServeMapper {
    @Insert("insert into\n" +
            "serve(serve_name,price,shop_id,type_id,state,sales)" +
            "values(#{serve_name},#{price},#{shop_id},#{type_id},#{state},#{sales})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(Serve serve);

    @Update("update serve set state=#{state} where id=#{id}")
    int updateState(Long id,int state);

    @Select("SELECT * FROM serve WHERE id = #{id}")
    Serve findById(long id);

    @Select("SELECT * FROM serve WHERE state = #{state} LIMIT #{size} OFFSET #{offset}")
    List<Serve> listByState(int state,int size,int offset);

    @Select("SELECT * \n" +
            "FROM serve \n" +
            "LIMIT #{size} OFFSET #{offset};")
    List<Serve> list(int size,int offset);

    @Update("update serve set sales=#{number} where id=#{id}")
    int updateSales(Long id, int number);
}
