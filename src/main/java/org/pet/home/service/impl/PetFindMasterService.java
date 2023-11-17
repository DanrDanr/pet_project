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
    public int add(PetFindMaster petFindMaster) {
        return petFindMasterMapper.add(petFindMaster);
    }

    @Override
    public PetFindMaster findById(Long id) {
        return petFindMasterMapper.findById(id);
    }

    @Override
    public List< PetFindMaster > findByState(int state, long employee_id) {
        return petFindMasterMapper.findByState(state, employee_id);
    }

    @Override
    public List< PetFindMaster > findByUser(int state, long user_id) {
        return petFindMasterMapper.findByUser(state, user_id);
    }

    @Override
    public int updateState(int state, long id) {
        return petFindMasterMapper.updateState(state, id);
    }

}
