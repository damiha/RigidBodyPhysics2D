package com.example.rigidbody2d;

public class Utils {

    public static boolean isClose(double x, double y){
        return Math.abs(x - y) <= 1e-4;
    }
}
