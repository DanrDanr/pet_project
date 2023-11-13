package org.pet.home.service.impl;

import org.pet.home.entity.*;
import org.pet.home.mapper.PetFindMasterMapper;
import org.pet.home.service.IPetFindMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public int add(Shop shop, Employee employee, PetCategory petCategory, User user, PetFindMaster petFindMaster) {
        return petFindMasterMapper.add(shop, employee, petCategory, user, petFindMaster);
    }

    @Override
    public PetFindMaster findById(Long id) {
        return petFindMasterMapper.findById(id);
    }

    @Override
    public List< PetFindMaster > findByState(int state) {
        return petFindMasterMapper.findByState(state);
    }

    @Override
    public void updateState(int state, long id) {
        petFindMasterMapper.updateState(state, id);
    }

}
