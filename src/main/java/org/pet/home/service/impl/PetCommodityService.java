package org.pet.home.service.impl;

import org.pet.home.entity.PetCommodity;
import org.pet.home.mapper.PetCommodityMapper;
import org.pet.home.service.IPetCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/15
 **/
@Service
public class PetCommodityService implements IPetCommodityService {
    private PetCommodityMapper petCommodityMapper;

    @Autowired
    public PetCommodityService(PetCommodityMapper petCommodityMapper) {
        this.petCommodityMapper = petCommodityMapper;
    }

    @Override
    public int add(PetCommodity petCommodity) {
        return petCommodityMapper.add(petCommodity);
    }

    @Override
    public PetCommodity findByPetFindMaster_id(Long id) {
        return petCommodityMapper.findByPetFindMaster_id(id);
    }

    @Override
    public List< PetCommodity > findByState(int state, long employee_id) {
        return petCommodityMapper.findByState(state, employee_id);
    }

    @Override
    public int updateState(int state, long id) {
        return petCommodityMapper.updateState(state, id);
    }

    @Override
    public List< PetCommodity > findByShop(long shop_id) {
        return petCommodityMapper.findByShop(shop_id);
    }

    @Override
    public PetCommodity check(Long id) {
        return petCommodityMapper.check(id);
    }

    @Override
    public int petAdopt(long user_id, long endTime,long id) {
        return petCommodityMapper.petAdopt(user_id, endTime, id);
    }

    @Override
    public List< PetCommodity > findByUser(long user_id) {
        return petCommodityMapper.findByUser(user_id);
    }
}
