package org.pet.home;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.utils.DateConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/14
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class timeTest {

    @Test
    public void timeTest(){
       long time = DateConverter.TimeConversion("2023年3月2日");
        System.out.println(time);
    }
}
