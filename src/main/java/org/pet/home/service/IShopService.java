package org.pet.home.service;

import org.apache.ibatis.annotations.Param;
import org.pet.home.entity.Shop;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/5
 **/
public interface IShopService {

    int add(Shop shop);

    List<Shop> list();

    int count();

    List<Shop> paginationList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    void updateState(Long id,int state);

    int delete(Long id);

    void update(Shop shop);
}
