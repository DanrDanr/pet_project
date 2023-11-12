package org.pet.home.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pet.home.entity.Employee;
import org.pet.home.entity.Location;
import org.pet.home.entity.Shop;
import org.pet.home.utils.AddressDistanceComparator;
import org.pet.home.utils.GaoDeMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/5
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopMapperTest {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    public void addShopTest(){
        Shop shop = new Shop();
        shop.setName("电子商务");
        shop.setTel("13339913684");
        shop.setRegisterTime(new Date().getTime());
        shop.setAddress("街道口");
        shop.setState(0);
        Employee employee = new Employee();
        employee.setId(1L);
        shop.setAdmin(employee);
        shop.setId(1L);

        shopMapper.add(shop);
        System.out.println(shop);
    }

    @Test
    public void listTest(){
        System.out.println(shopMapper.list());
    }
    @Test
    public void near() throws UnsupportedEncodingException {
        List<Shop> shops = shopMapper.list();
        Location location = GaoDeMapUtil.getLngAndLag("湖北省黄石市大冶市周家湾");
        List<Location>locations = new LinkedList<>();
        for (int i=0;i<shops.size();i++){
            locations.add(i,GaoDeMapUtil.getLngAndLag(shops.get(i).getAddress()));
        }
        Location near = AddressDistanceComparator.findNearestAddress(location,locations);
        System.out.println(near);
        System.out.println(shopMapper.findByAddress(near.getFormattedAddress()));
        Shop shop = shopMapper.findByAddress(near.getFormattedAddress());
        //根据店铺获取要绑定的shop_admin的账号
        Employee admin = employeeMapper.findById(shop.getAdmin_id());
        System.out.println(admin);
    }
}
