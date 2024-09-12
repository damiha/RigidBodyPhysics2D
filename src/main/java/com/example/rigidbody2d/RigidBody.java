package com.example.rigidbody2d;

public abstract class RigidBody {

    Vector3 position;
    Vector3 velocity;
    Vector3 acceleration;

    double angle;
    double inverseMass;
    double inverseMomentOfInertia;
    boolean isStatic;

    public RigidBody(double centerX, double centerY,
                     double angle, double inverseMass,
                     double inverseMomentOfInertia, boolean isStatic){

        position = new Vector3(centerX, centerY, 0);
        this.angle = angle;
        this.inverseMass = inverseMass;
        this.inverseMomentOfInertia = inverseMomentOfInertia;
        this.isStatic = isStatic;

        velocity = new Vector3();
        acceleration = new Vector3();
    }
}
