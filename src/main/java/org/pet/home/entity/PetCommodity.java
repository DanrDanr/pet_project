package org.pet.home.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/15
 **/
@Data
public class PetCommodity {
    private Long id;
    private String petName;
    private int sex;//0是母的 1是公的
    private Long sellTime;//开售时间
    private Long endTime;//售卖时间
    private int birth;
    private BigDecimal costPrice;//成本价
    private BigDecimal sellPrice;//售价
    private int isInoculation;//0是没接种 1是接种了
    private int state=0;//0已上架 1是没上架
    private int adopt=0;//0没领养 1是领养
    private Long user_id;//对应用户id
    private Long shop_id;//对应商家id
    private Long employee_id;//对应商品管理员id
    private Long petCategory_id;//对应宠物类型
    private Long petFindMaster_id;//对应寻主任务id
}
