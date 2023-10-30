package org.pet.home.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 22866
 * @date: 2023/10/26
 **/
@RestController
public class HelloController {

    @RequestMapping("/helloPetHome")
    public String helloPetHome(){
        return "Hello PetHome";
    }
}
