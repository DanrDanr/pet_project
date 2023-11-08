package org.pet.home.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.pet.home.entity.Employee;
import org.pet.home.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/7
 **/
@Mapper
@Repository
public interface UserMapper {

    @Insert("insert into t_user(username,email,salt,phone,password,age,state,createtime,headImg)" +
            "values(#{username},#{email},#{salt},#{phone},#{password},#{age},#{state},#{createtime},#{headImg})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int add(User user);

    /**
     * 检查号码是否存在
     * @param phone
     * @return
     */
    @Select("SELECT * FROM t_user WHERE phone = #{phone}")
    User checkPhone(String phone);

    @Select("select * from t_user where phone=#{phone} and password=#{password}")
    User login(String phone,String password);
}
