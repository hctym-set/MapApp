package com.example.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Poiinfo {
    private int id;
    private String name;
    private Double longitude;
    private Double latitude;
    private String message;
}
