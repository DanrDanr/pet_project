package org.pet.home.mapper;

import org.apache.ibatis.annotations.*;
import org.pet.home.entity.Employee;
import org.pet.home.entity.Shop;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/5
 **/
@Mapper
@Repository
public interface ShopMapper {
    /**
     * 新增商家
     * @param shop
     * @return
     */

    @Insert("insert into\n" +
            "t_shop(name,tel,registerTime,state,address,admin_id)" +
            "values(#{name},#{tel},#{registerTime},#{state},#{address},#{admin.id})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(Shop shop);

    /**
     * 查看所有商家
     * @return
     */
    @Select("SELECT * FROM t_shop")
    List<Shop> list();

    /**
     * 查看数据条数
     * @return
     */
    @Select("SELECT COUNT(*) FROM t_shop")
    int count();

    /**
     * 分页查询
     * @param offset
     * @param pageSize
     * @return
     */

    @Select("SELECT * FROM t_shop LIMIT #{offset}, #{pageSize}")
    List<Shop> paginationList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 修改商家状态 0未审核 1审核成功 2审核失败
     * @param id
     */
    @Update("update t_shop set state=#{state} where id=#{id}")
    void updateState(Long id,int state);

    /**
     * 删除商家
     * @param id
     * @return
     */
    @Delete("delete from t_shop where id=#{id}")
    int delete(Long id);

    /**
     * 修改商家数据
     * @param shop
     */
    @Update("update t_shop set name=#{name},tel=#{tel},state=#{state},address=#{address} where id=#{id}")
    void update(Shop shop);
}
