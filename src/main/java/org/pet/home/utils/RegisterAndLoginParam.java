package org.pet.home.utils;

import lombok.Data;
import org.pet.home.entity.User;

/**
 * {
 *     phone:xx,
 *     psd:xx,
 *     code:xx
 * }
 *
 * {
 *     user:{}
 *
 * }
 * {
 *     user:{
 *         phone:xx,
 *         psd,xx
 *     },
 *     code:xx
 * }
 * @description:
 * @author: 22866
 * @date: 2023/11/9
 **/
@Data
public class RegisterAndLoginParam {
    public String username;
    public String password;
    public String email;
    public String phone;
    public String code;
}
