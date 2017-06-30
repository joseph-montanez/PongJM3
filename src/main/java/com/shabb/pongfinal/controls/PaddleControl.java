package com.shabb.pongfinal.controls;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

public class PaddleControl extends RigidBodyControl implements PhysicsCollisionListener {
    private PhysicsGhostObject ghostObject;
    private Vector3f vector = new Vector3f();

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

    @Override
    public void setPhysicsSpace(PhysicsSpace space) {
        super.setPhysicsSpace(space);
        if (space != null) {
            space.addCollisionListener(this);
        }
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        PhysicsCollisionObject objA = event.getObjectA();
        PhysicsCollisionObject objB = event.getObjectB();
        Class<WallControl> wallCtrlClass = WallControl.class;

        //-- Test for collision against ball and paddle
        boolean collisionWithPaddle = objA == this || objB == this;
        boolean collisionWithWall = objA.getClass() == wallCtrlClass || objB.getClass() == wallCtrlClass;
        if (collisionWithPaddle && collisionWithWall) {
            //-- Figure out which object is the paddle
            Spatial paddle = (Spatial) ((objA == this) ? objA.getUserObject() : objB.getUserObject());

            //-- Look at the lateral friction to determine the direction of the y-axis to limit
            Vector3f lateralFriction = event.getLateralFrictionDir1();
            if (lateralFriction.getY() > 0.0f) {
                paddle.setUserData("hitTopWall", true);
                paddle.setUserData("hitBottomWall", false);
            } else {
                paddle.setUserData("hitTopWall", false);
                paddle.setUserData("hitBottomWall", true);
            }
        }

    }
}
