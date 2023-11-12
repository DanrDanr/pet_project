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
    int add(PetFindMaster petFindMaster);

    int addTask(@Param("shop") Shop shop, @Param("employee") Employee employee
            , @Param("petCategory")PetCategory petCategory, @Param("user")User user,@Param("petFindMaster")PetFindMaster petFindMaster);
}
