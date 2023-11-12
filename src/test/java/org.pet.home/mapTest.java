package org.pet.home;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.common.UserLoginInterceptor;
import org.pet.home.entity.Location;
import org.pet.home.utils.AddressDistanceComparator;
import org.pet.home.utils.DistanceCalculator;
import org.pet.home.utils.GaoDeMapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class mapTest {
    private Logger logger = LoggerFactory.getLogger(mapTest.class);

    @Test
    public void MapTest() throws UnsupportedEncodingException {
        try {
            Location location = GaoDeMapUtil.getLngAndLag("湖北省黄石市大冶市桔园小区1栋");
            System.out.println(location);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void nearMap(){
        List<Location> list = new ArrayList<>(); // 实例化列表
        try {
            Location user = GaoDeMapUtil.getLngAndLag("湖北省黄石市大冶市桔园小区1栋");
            Location location = GaoDeMapUtil.getLngAndLag("湖北省黄石市万达广场");
            Location location1 = GaoDeMapUtil.getLngAndLag("湖北省武汉市光谷广场");
            Location location2 = GaoDeMapUtil.getLngAndLag("湖北省黄石市大冶市雨润广场");
            logger.info(String.valueOf(location));
            logger.info(String.valueOf(location1));
            logger.info(String.valueOf(location2));
            list.add(0,location);
            list.add(1,location1);
            list.add(2,location2);
            System.out.println(DistanceCalculator.calculateDistance(user,location1));
            System.out.println(AddressDistanceComparator.findNearestAddress(user,list));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
