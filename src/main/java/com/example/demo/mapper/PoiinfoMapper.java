package com.example.demo.mapper;

import com.example.demo.pojo.Poiinfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PoiinfoMapper {
    public List<Poiinfo> getAll();
}
