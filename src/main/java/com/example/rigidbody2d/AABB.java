package com.example.rigidbody2d;

public class AABB {

    double minX, minY, maxX, maxY;

    public AABB(double minX, double minY, double maxX, double maxY){
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean isIntersecting(AABB other){
        boolean xOverlap = this.minX <= other.maxX && this.maxX >= other.minX;
        boolean yOverlap = this.minY <= other.maxY && this.maxY >= other.minY;
        return xOverlap && yOverlap;
    }
}
