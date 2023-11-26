package org.pet.home.service;

import org.pet.home.entity.User;
import org.pet.home.utils.NetResult;
import org.pet.home.utils.UserParam;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/6
 **/
public interface IUserService {
    int add(User user);
    User checkPhone(String phone);
    User userLogin(String phone,String password);
    User findById(Long id);

    /**
     *
     * @param phone
     * @return
     */
    NetResult sendRegisterCode(String phone);

    NetResult login(UserParam userParam);
    int recharge(Long id,double balance);
    int pay(Long id,double balance);

}
