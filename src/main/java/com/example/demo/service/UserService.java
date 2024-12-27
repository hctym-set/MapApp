package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public List<Users> getAll()
    {
        return userMapper.getAll();
    }
    public List<Users> getByName(String username)
    {
        return userMapper.getByName(username);
    }
    public Users getByUsernameAndPassword(String username,String password)
    {
        return userMapper.getByUsernameAndPassword(username,password);
    }
}
