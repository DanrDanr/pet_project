package org.pet.home.service.impl;

import org.pet.home.entity.PetCategory;
import org.pet.home.mapper.PetCategoryMapper;
import org.pet.home.service.IPetCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@Service
public class PetCategoryService implements IPetCategoryService {
    private PetCategoryMapper petCategoryMapper;

    @Autowired
    public PetCategoryService(PetCategoryMapper petCategoryMapper) {
        this.petCategoryMapper = petCategoryMapper;
    }

    @Override
    public int add(PetCategory petCategory) {
        return petCategoryMapper.add(petCategory);
    }

    @Override
    public List< PetCategory > list() {
        return petCategoryMapper.list();
    }

    @Override
    public PetCategory findById(Long id) {
        return petCategoryMapper.findById(id);
    }
}
