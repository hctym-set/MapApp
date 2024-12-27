package com.example.demo.controller;

import com.example.demo.service.PoiinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PoiinfoController {
    @Autowired
    PoiinfoService poiinfoService;

    @RequestMapping("/getpoints")
    public Object getPoints(){
        return poiinfoService.getAll();
    }
}
