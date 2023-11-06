package org.pet.home.service;

import org.pet.home.utils.NetResult;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/6
 **/
public interface IUserService {
    /**
     *
     * @param phone
     * @return
     */
    NetResult sendRegisterCode(String phone);
}
