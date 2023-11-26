package org.pet.home.service.impl;

import org.pet.home.entity.Type;
import org.pet.home.mapper.TypeMapper;
import org.pet.home.service.ITypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/26
 **/
@Service
public class TypeService implements ITypeService {
    private TypeMapper typeMapper;

    @Autowired
    public TypeService(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public Type findById(long id) {
        return typeMapper.findById(id);
    }
}
