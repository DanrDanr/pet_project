package org.pet.home.utils;

import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/29
 **/
@Data
public class DepartmentParam {
    private String sn;
    private String name;
    private String dirPath;
    private  int state;
    private long parentId;

}
