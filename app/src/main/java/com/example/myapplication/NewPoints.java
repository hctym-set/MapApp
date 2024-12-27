package com.example.myapplication;

public class NewPoints {
    public String name;
    public double latitude;
    public double longitude;
    public String message;

    // 可选：添加构造方法
    public NewPoints(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.message=message;
    }

    // 可选：添加 toString 方法
    @Override
    public String toString() {
        return "NewPoints{name='" + name + "', latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}

