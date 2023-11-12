package org.pet.home;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.AddressInfo;
import org.pet.home.utils.GaoDeMapUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class mapTest {

    @Test
    public void MapTest() throws UnsupportedEncodingException {
        try {
            AddressInfo addressInfo = GaoDeMapUtil.getLngAndLag("江西省南昌市青山湖区");
            System.out.println(addressInfo);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
