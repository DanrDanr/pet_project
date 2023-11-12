package org.pet.home.service;

import org.pet.home.entity.PetCategory;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
public interface IPetCategoryService {
    int add(PetCategory petCategory);

    List< PetCategory > list();

    PetCategory findById(Long id);
}
