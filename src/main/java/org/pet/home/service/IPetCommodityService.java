package org.pet.home.service;

import org.apache.ibatis.annotations.Select;
import org.pet.home.entity.PetCommodity;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/15
 **/
public interface IPetCommodityService {
    int add(PetCommodity petCommodity);
    PetCommodity findByPetFindMaster_id(Long id);
    List< PetCommodity > findByState(int state, long employee_id);
    int updateState(int state,long id);
    List< PetCommodity > findByShop(long shop_id);
    PetCommodity check(Long id);
    int petAdopt(long user_id,long endTime,long id);
    List< PetCommodity > findByUser(long user_id);
}
