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
    private Material mat;
    private BoxCollisionShape boxShape;
    private RigidBodyControl control;

    public Paddle(String name, Spatial obj) {
        this.name = name;
        this.obj = obj;
    }

    public void initGraphics(AssetManager assetManager, Node rootNode) {
        mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        obj.setMaterial(mat);
        obj.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    public void update(float tpf, BulletAppState bulletAppState) {
        //-- Update control (physics object) with current location and rotation
        if (control != null) {
            control.setPhysicsRotation(obj.getWorldRotation());
            control.setPhysicsLocation(obj.getWorldTranslation());
        }
    }

    public void addLocalTranslation(float x, float y, float z) {
        Vector3f v = obj.getLocalTranslation();
        obj.setLocalTranslation(v.x + x, v.y + y, v.z + z);
    }

    public void initPhysics(BulletAppState bulletAppState) {
        Vector3f dimensions = ((BoundingBox) obj.getWorldBound()).getExtent(null);
        boxShape = new BoxCollisionShape(dimensions);
        control = new PaddleControl(boxShape);
        control.setPhysicsRotation(obj.getWorldRotation());
        control.setPhysicsLocation(obj.getWorldTranslation());
        obj.addControl(control);
        bulletAppState.getPhysicsSpace().add(obj);
    }
}
