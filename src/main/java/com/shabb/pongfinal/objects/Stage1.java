package com.shabb.pongfinal.objects;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.Optional;

public class Stage1 {
    private Spatial floor;
    private Spatial wall1;
    private Spatial wall2;
    private Spatial wall3;
    private Spatial wall4;
    private Spatial ball;
    private Spatial player1;
    private Spatial player2;

    public Spatial getFloor() {
        return floor;
    }

    public Spatial getBall() {
        return ball;
    }

    public Spatial getPlayer1() {
        return player1;
    }

    public Spatial getPlayer2() {
        return player2;
    }

    public Spatial getWall1() {
        return wall1;
    }

    public Spatial getWall2() {
        return wall2;
    }

    public Spatial getWall3() {
        return wall3;
    }

    public Spatial getWall4() {
        return wall4;
    }

    public Stage1() {

    }

    public void initGraphics(AssetManager assetManager, Node rootNode) {
        Spatial box = assetManager.loadModel("Scenes/stage-1.j3o");
        rootNode.attachChild(box);

        rootNode.breadthFirstTraversal(spatial -> {
            Optional<String> name = Optional.ofNullable(spatial.getName());
            switch (name.orElse("")) {
                case "Player1" + "1":
                    player1 = spatial;
                    break;
                case "Player2" + "1":
                    player2 = spatial;
                    break;
                case "Floor" + "1":
                    floor = spatial;
                    break;
                case "Ball" + "1":
                    ball = spatial;
                    break;
                case "WallTop" + "1":
                    wall1 = spatial;
                    break;
                case "WallBottom" + "1":
                    wall2 = spatial;
                    break;
                case "WallRight" + "1":
                    wall3 = spatial;
                    break;
                case "WallLeft" + "1":
                    wall4 = spatial;
                    break;
            }
        });
    }
}
