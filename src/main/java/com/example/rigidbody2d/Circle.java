package com.example.rigidbody2d;

import java.util.ArrayList;
import javafx.scene.paint.Color;


public class Circle extends RigidBody{

    static final double DENSITY = 1.0;
    double radius;

    public Circle(double centerX, double centerY, double radius, boolean isStatic) {

        super(centerX, centerY, 0, 0.0, 0.0, isStatic);

        this.radius = radius;

        updatePhysicalProperties();
    }
    private void updatePhysicalProperties(){
        double mass = Math.PI * radius * radius * DENSITY;
        inverseMass = isStatic ? 0.0 : (1.0 / mass);
        inverseMomentOfInertia = isStatic ? 0.0 : (2.0 / (mass * radius * radius));
    }

    public void setRadius(double newRadius){
        this.radius = newRadius;
        updatePhysicalProperties();
    }
}
