package org.pet.home.service.impl;

import org.pet.home.entity.*;
import org.pet.home.mapper.PetFindMasterMapper;
import org.pet.home.service.IPetFindMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@Service
public class PetFindMasterService implements IPetFindMasterService {
    private PetFindMasterMapper petFindMasterMapper;

    @Autowired
    public PetFindMasterService(PetFindMasterMapper petFindMasterMapper) {
        this.petFindMasterMapper = petFindMasterMapper;
    }

    @Override
    public int add(PetFindMaster petFindMaster) {
        return petFindMasterMapper.add(petFindMaster);
    }

    @Override
    public int addTask(Shop shop, Employee employee, PetCategory petCategory, User user,PetFindMaster petFindMaster) {
        return petFindMasterMapper.addTask(shop, employee, petCategory, user,petFindMaster);
    }
}
