package com.example.rigidbody2d;

public class Vector3 {
    double x, y, z;

    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(){
        x = y = z = 0;
    }

    public void normalize(){
        double norm = norm();

        x /= norm;
        y /= norm;
        z /= norm;
    }

    public double norm(){
        return Math.sqrt(x * x + y * y + z * z);
    }

    public void add(Vector3 v){
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public static Vector3 add(Vector3 v, Vector3 w){
        return new Vector3(v.x + w.x, v.y + w.y, v.z + w.z);
    }

    public static Vector3 cross(Vector3 v, Vector3 w) {
        double crossX = v.y * w.z - v.z * w.y;
        double crossY = v.z * w.x - v.x * w.z;
        double crossZ = v.x * w.y - v.y * w.x;

        return new Vector3(crossX, crossY, crossZ);
    }

    public static Vector3 sub(Vector3 v, Vector3 w){
        return new Vector3(v.x - w.x, v.y - w.y, v.z - w.z);
    }

    public static double dot(Vector3 v, Vector3 w){
        return (v.x * w.x) + (v.y * w.y) + (v.z * w.z);
    }

    public static Vector3 rotateAroundZAxis(Vector3 v, double rad){
        return new Vector3(v.x * Math.cos(rad) - v.y * Math.sin(rad), v.x * Math.sin(rad) + v.y * Math.cos(rad), 0);
    }

    public static Vector3 mul(double s, Vector3 v){
        return new Vector3(s * v.x, s * v.y, s * v.z);
    }

    public String toString(){
        return String.format("(%.3f, %.3f, %.3f", x, y, z);
    }
}
