package org.pet.home.service.impl;

import org.pet.home.entity.Serve;
import org.pet.home.mapper.ServeMapper;
import org.pet.home.service.IServeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/26
 **/
@Service
public class ServeService implements IServeService {
    private ServeMapper serveMapper;

    @Autowired
    public ServeService(ServeMapper serveMapper) {
        this.serveMapper = serveMapper;
    }

    @Override
    public int add(Serve serve) {
        return serveMapper.add(serve);
    }

    @Override
    public int updateState(Long id, int state) {
        return serveMapper.updateState(id, state);
    }

    @Override
    public Serve findById(long id) {
        return serveMapper.findById(id);
    }

    @Override
    public List< Serve > listByState(int state, int size, int offset) {
        return serveMapper.listByState(state, size, offset);
    }
}
