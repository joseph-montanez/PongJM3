package com.shabb.pongfinal.controls;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;

public class BallControl extends RigidBodyControl implements PhysicsCollisionListener, PhysicsTickListener {
    private float radius;
    private PhysicsGhostObject ghostObject;
    private Vector3f vector = new Vector3f();

    public BallControl(CollisionShape shape, float radius) {
        super(shape);

        //-- Set initial ball velocity
        setLinearVelocity(new Vector3f(20f,0f,0f));
        //-- Do not slow down the ball when sliding
        setFriction(0f);
        //-- Apply as little mass as possible to prevent the ball from stopping
        setMass(0.001f);
        //-- Restitution is how bounce the object is
        setRestitution(1.0f);
        //-- Prevent the ball from flying through paddles
        setCcdMotionThreshold(1.0f);

        this.radius = radius;

        createGhostObject();
    }

    @Override
    public void setPhysicsSpace(PhysicsSpace space) {
        super.setPhysicsSpace(space);
        if (space != null) {
            space.addCollisionListener(this);
        }
    }

    protected void createGhostObject() {
        ghostObject = new PhysicsGhostObject(new SphereCollisionShape(radius + 0.5f));
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        PhysicsCollisionObject objA = event.getObjectA();
        PhysicsCollisionObject objB = event.getObjectB();
        Class<PaddleControl> paddleCtrlClass = PaddleControl.class;

        //-- Test for collision against ball and paddle
        boolean collisionWithBall = objA == this || objB == this;
        boolean collisionWithPaddle = objA.getClass() == paddleCtrlClass || objB.getClass() == paddleCtrlClass;
        if (collisionWithBall && collisionWithPaddle) {
            //-- Ball and paddle has collided lets add a ghost object to detect any other collisions
            ghostObject.setPhysicsLocation(getPhysicsLocation(vector));
            space.add(ghostObject);
            space.addTickListener(this);
        }

    }

    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        Vector3f location = getPhysicsLocation();
        Vector3f impulse = Vector3f.ZERO;

        for (PhysicsCollisionObject collisionObj : ghostObject.getOverlappingObjects()) {
            if (collisionObj instanceof PhysicsRigidBody && collisionObj.getClass() == PaddleControl.class) {
                PhysicsRigidBody paddleCtrl = (PhysicsRigidBody) collisionObj;
                Vector3f vector2 = paddleCtrl.getPhysicsLocation();
                //-- Get the local location from ball and paddle
                vector2.subtractLocal(location);
                vector2.normalizeLocal();
                //-- Get the up / down position to decide on the force to apply
                float z = vector2.getZ();
                float zForce = -1 * z / 100;
                float xForce = vector2.getX() > 0 ? -0.0005f : 0.0005f;
                impulse = new Vector3f(xForce, 0, zForce);
            }
        }
        this.applyImpulse(impulse, Vector3f.ZERO);
        System.out.println(impulse);
    }

    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        //-- Remove ghost object when we are done testing the ghost object for other collisions
        space.removeTickListener(this);
        space.remove(ghostObject);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
