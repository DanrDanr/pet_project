package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.PetFindMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class PetFindMasterTest {
    private Logger logger = LoggerFactory.getLogger(PetFindMasterTest.class);
    @Autowired
    private PetFindMasterMapper petFindMasterMapper;

    @Test
    public void addTest(){
        PetFindMaster petFindMaster = new PetFindMaster();
        petFindMaster.setPetName("花花");
        petFindMaster.setAddress("湖北省黄石市大冶市周家湾");
        petFindMaster.setSex(0);
        petFindMaster.setIsInoculation(1);
        petFindMaster.setPrice(BigDecimal.valueOf(70.23));
        petFindMaster.setCreateTime(System.currentTimeMillis());
        petFindMaster.setBirth(2);
        petFindMasterMapper.add(petFindMaster);
    }

    @Test
    public void setTest(){

    }
}
