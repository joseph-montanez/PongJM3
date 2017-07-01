package com.shabb.pongfinal.objects

import com.jme3.asset.AssetManager
import com.jme3.scene.Node
import com.jme3.scene.Spatial

import java.util.Optional

class Stage1 {
    var floor: Spatial? = null
        private set
    var wall1: Spatial? = null
        private set
    var wall2: Spatial? = null
        private set
    var wall3: Spatial? = null
        private set
    var wall4: Spatial? = null
        private set
    var ball: Spatial? = null
        private set
    var player1: Spatial? = null
        private set
    var player2: Spatial? = null
        private set

    fun initGraphics(assetManager: AssetManager, rootNode: Node) {
        val box = assetManager.loadModel("Scenes/stage-1.j3o")
        rootNode.attachChild(box)

        rootNode.breadthFirstTraversal { spatial ->
            val name = Optional.ofNullable(spatial.name)
            when (name.orElse("")) {
                "Player1" + "1" -> player1 = spatial
                "Player2" + "1" -> player2 = spatial
                "Floor" + "1" -> floor = spatial
                "Ball" + "1" -> ball = spatial
                "WallTop" + "1" -> wall1 = spatial
                "WallBottom" + "1" -> wall2 = spatial
                "WallRight" + "1" -> wall3 = spatial
                "WallLeft" + "1" -> wall4 = spatial
            }
        }
    }
}
