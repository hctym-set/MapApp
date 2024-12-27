package com.example.demo.controller;

import com.example.demo.pojo.Users;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/getall")
    public Object getAll()
    {
        return userService.getAll();
    }
    @RequestMapping("/getbyname")
    public Object getByName(String username)
    {
        return userService.getByName(username);
    }
    @RequestMapping("/login")
    public Users getByUsernameAndPassword(String username,String password)
    {
        return userService.getByUsernameAndPassword(username,password);
    }
}
