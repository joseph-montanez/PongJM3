package com.shabb.pongfinal.controls;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

/**
 * Created by xingo on 6/17/2017.
 */
public class PaddleControl extends RigidBodyControl {
    public PaddleControl(CollisionShape shape) {
        super(shape);

        //-- Setup object as Kinematic
        setKinematic(true);
        //-- Apply zero friction to not slow down the ball
        setFriction(0f);
        //-- Allow the ball to bounce off
        setRestitution(1f);
        setMass(1000f);
    }
}
