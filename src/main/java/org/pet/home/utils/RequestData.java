package org.pet.home.utils;

import lombok.Data;
import org.pet.home.entity.Shop;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/6
 **/
@Data
public class RequestData {
    private int state;
    private Shop shop;
}
