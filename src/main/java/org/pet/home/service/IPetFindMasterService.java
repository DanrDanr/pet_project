package org.pet.home.service;

import org.apache.ibatis.annotations.Param;
import org.pet.home.entity.*;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
public interface IPetFindMasterService {
    int add(Shop shop, Employee employee, PetCategory petCategory,User user,PetFindMaster petFindMaster);
    PetFindMaster findById(Long id);
    List<PetFindMaster> findByState(int state,long employee_id);
    List<PetFindMaster> findByUser(int state,long user_id);
    int updateState(int state,long id);


}
