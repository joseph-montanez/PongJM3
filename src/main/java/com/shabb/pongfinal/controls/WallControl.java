package com.shabb.pongfinal.controls;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

public class WallControl extends RigidBodyControl {
    public WallControl(CollisionShape shape) {
        super(shape);

        //-- Setup object as static
        setMass(0f);
        setKinematic(false);
        //-- Do not slow the ball when sliding against the floor.
        setFriction(0f);
        setRestitution(1f);
    }
}
