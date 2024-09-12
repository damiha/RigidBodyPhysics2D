package com.example.rigidbody2d;

import javafx.scene.canvas.GraphicsContext;

public abstract class RigidBody {

    Vector3 position;
    double angle;
    double inverseMass;
    double inverseMomentOfInertia;

    // if 'suspended == true' then the body is temporarily not experiencing physics
    boolean suspended = false;

    public RigidBody(double centerX, double centerY, double angle, double inverseMass, double inverseMomentOfInertia){
        position = new Vector3(centerX, centerY, 0);
        this.angle = angle;
        this.inverseMass = inverseMass;
        this.inverseMomentOfInertia = inverseMomentOfInertia;
    }
}
