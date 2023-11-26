package org.pet.home.service;

import org.pet.home.entity.Serve;

import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/26
 **/
public interface IServeService {
    int add(Serve serve);
    int updateState(Long id,int state);
    Serve findById(long id);
    List<Serve> listByState(int state, int size, int offset);
}
