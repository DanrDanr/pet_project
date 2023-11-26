package org.pet.home.entity;

import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/26
 **/
@Data
public class Serve {
    private long id;
    private String serve_name;
    private double price;
    private int state=0;
    private long shop_id;
    private long type_id;
}
