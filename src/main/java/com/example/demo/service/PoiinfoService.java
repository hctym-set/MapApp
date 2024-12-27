package com.example.demo.service;

import com.example.demo.mapper.PoiinfoMapper;
import com.example.demo.pojo.Poiinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoiinfoService {
    @Autowired
    PoiinfoMapper poiinfoMapper;

    public List<Poiinfo> getAll(){
        return poiinfoMapper.getAll();
    }
}
