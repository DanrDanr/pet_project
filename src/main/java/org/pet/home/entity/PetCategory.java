package org.pet.home.entity;

import lombok.Data;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
@Data
public class PetCategory {
    private Long id;
    private String petType;
    private String description;
    // 构造函数
    public PetCategory(String petType, String description) {
        this.petType = petType;
        this.description = description;
    }
}
