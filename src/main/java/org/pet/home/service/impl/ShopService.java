package org.pet.home.service.impl;

import org.pet.home.entity.Shop;
import org.pet.home.mapper.ShopMapper;
import org.pet.home.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/5
 **/
@Service
public class ShopService implements IShopService {
    private ShopMapper shopMapper;

    @Autowired
    public ShopService(ShopMapper shopMapper){
        this.shopMapper = shopMapper;
    }
    @Override
    public int add(Shop shop) {
        return shopMapper.add(shop);
    }

    @Override
    public List< Shop > list() {
        return shopMapper.list();
    }

    @Override
    public int count() {
        return shopMapper.count();
    }

    @Override
    public List< Shop > paginationList(int offset, int pageSize) {
        return shopMapper.paginationList(offset,pageSize);
    }

    @Override
    public void updateState(Long id, int state) {
        shopMapper.updateState(id, state);
    }

    @Override
    public int delete(Long id) {
        return shopMapper.delete(id);
    }

    @Override
    public void update(Shop shop) {
        shopMapper.update(shop);
    }
}
