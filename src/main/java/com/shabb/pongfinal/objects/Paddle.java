package com.shabb.pongfinal.objects;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.shabb.pongfinal.controls.PaddleControl;

public class Paddle {
    private final Spatial obj;
    private String name;
    private RigidBodyControl control;
    private Vector3f pendingMove;

    public Paddle(String name, Spatial obj) {
        this.name = name;
        this.obj = obj;
    }

    public void initGraphics(AssetManager assetManager, Node rootNode) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        obj.setMaterial(mat);
        obj.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    public void update(float tpf, BulletAppState bulletAppState) {
        //-- Review the pending move to see if the paddle has hit a wall
        if (pendingMove != null) {
            Boolean hitTopWall = obj.getUserData("hitTopWall");
            Boolean hitBottomWall = obj.getUserData("hitBottomWall");

            //-- If the paddle has hit the wall, then zero out the direction it hit
            if (hitTopWall != null && hitTopWall) {
                pendingMove.setZ(Math.min(0.0f, pendingMove.getZ()));
                obj.setUserData("hitTopWall", false);
            } else if (hitBottomWall != null && hitBottomWall) {
                pendingMove.setZ(Math.max(0.0f, pendingMove.getZ()));
                obj.setUserData("hitBottomWall", false);
            }

            //-- Apply the movement
            Vector3f v = obj.getLocalTranslation().add(pendingMove);
            obj.setLocalTranslation(v.x, v.y, v.z);

            //-- Reset pending movement
            pendingMove = null;
        }

        //-- Update control (physics object) with current location and rotation
        if (control != null) {
            control.setPhysicsRotation(obj.getWorldRotation());
            control.setPhysicsLocation(obj.getWorldTranslation());
        }
    }

    public void addLocalTranslation(float x, float y, float z) {
        pendingMove = new Vector3f(x, y, z);
    }

    public void initPhysics(BulletAppState bulletAppState) {
        Vector3f dimensions = ((BoundingBox) obj.getWorldBound()).getExtent(null);
        BoxCollisionShape boxShape = new BoxCollisionShape(dimensions);
        control = new PaddleControl(boxShape);
        control.setPhysicsRotation(obj.getWorldRotation());
        control.setPhysicsLocation(obj.getWorldTranslation());
        obj.addControl(control);
        bulletAppState.getPhysicsSpace().add(obj);
    }
}
