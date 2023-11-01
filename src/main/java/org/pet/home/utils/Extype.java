package org.pet.home.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/1
 **/
@Data
@ApiModel(value ="类型实体")
public class Extype {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "类型名字")
    private String name;
}
