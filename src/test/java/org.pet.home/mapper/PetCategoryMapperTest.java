package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.PetCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class PetCategoryMapperTest {
    private Logger logger = LoggerFactory.getLogger(PetCategoryMapperTest.class);

    @Autowired
    private PetCategoryMapper petCategoryMapper;

    @Test
    public void addTest(){
        List< PetCategory > petCategories = new ArrayList<>();

        // 添加几条示例数据
        petCategories.add(new PetCategory("狗", "忠诚的动物，是人类的好朋友"));
        petCategories.add(new PetCategory( "猫", "独立的动物，喜欢独自活动"));
        petCategories.add(new PetCategory( "鸟", "喜欢飞翔的动物，有美丽的羽毛"));
    }

    @Test
    public void listTest(){
        logger.info(petCategoryMapper.list().toString());
    }
}
