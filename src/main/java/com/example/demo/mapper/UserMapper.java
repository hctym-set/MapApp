package com.example.demo.mapper;

import com.example.demo.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    public List<Users> getAll();
    public List<Users> getByName(String username);
    public Users getByUsernameAndPassword(@Param("username")String username,@Param("password") String password);
}
