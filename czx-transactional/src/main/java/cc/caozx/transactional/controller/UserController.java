package cc.caozx.transactional.controller;

import cc.caozx.transactional.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("wrong1")
    public int wrong1(@RequestParam("name") String name){
        return userService.createUserWrong1(name);
    }

    @GetMapping("wrong2")
    public int wrong2(@RequestParam("name") String name){
        return userService.createUserWrong2(name);
    }

}
