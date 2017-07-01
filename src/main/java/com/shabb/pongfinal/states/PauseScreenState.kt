package com.shabb.pongfinal.states

import com.jme3.app.Application
import com.jme3.app.SimpleApplication
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.asset.AssetManager
import com.jme3.audio.AudioRenderer
import com.jme3.input.InputManager
import com.jme3.math.ColorRGBA
import com.jme3.niftygui.NiftyJmeDisplay
import com.jme3.renderer.ViewPort
import com.jme3.scene.Node
import de.lessvoid.nifty.Nifty
import de.lessvoid.nifty.screen.Screen
import de.lessvoid.nifty.screen.ScreenController

class PauseScreenState(app: SimpleApplication) : AbstractAppState(), ScreenController {

    private val guiViewPort: ViewPort
    private val inputManager: InputManager
    private val audioRenderer: AudioRenderer
    private var app: SimpleApplication? = null
    private val viewPort: ViewPort
    private val rootNode: Node
    private val guiNode: Node
    private val assetManager: AssetManager
    private val localRootNode = Node("Start Screen RootNode")
    private val localGuiNode = Node("Start Screen GuiNode")
    private val backgroundColor = ColorRGBA.Gray

    private var niftyDisplay: NiftyJmeDisplay? = null
    private var nifty: Nifty? = null

    init {
        this.rootNode = app.rootNode
        this.viewPort = app.viewPort
        this.guiNode = app.guiNode
        this.inputManager = app.inputManager
        this.audioRenderer = app.audioRenderer
        this.assetManager = app.assetManager
        this.guiViewPort = app.guiViewPort
    }

    override fun initialize(stateManager: AppStateManager?, app: Application?) {
        super.initialize(stateManager, app)
        this.app = app as SimpleApplication?

        //-- Disable a few things!
        this.app!!.setDisplayFps(false)
        this.app!!.setDisplayStatView(false)
        this.app!!.flyByCamera.isEnabled = false

        rootNode.attachChild(localRootNode)
        guiNode.attachChild(localGuiNode)
        viewPort.backgroundColor = backgroundColor

        /** init the screen  */
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort)
        nifty = niftyDisplay!!.nifty
        nifty!!.fromXml("Interface/PauseMenu.xml", "GScreen0", this)
        guiViewPort.addProcessor(niftyDisplay!!)
    }

    override fun update(tpf: Float) {
        /** any main loop action happens here  */
        nifty!!.update()
    }

    override fun cleanup() {
        rootNode.detachChild(localRootNode)
        guiNode.detachChild(localGuiNode)

        niftyDisplay!!.cleanup()
        nifty!!.exit()

        super.cleanup()
    }

    override fun bind(nifty: Nifty, screen: Screen) {

    }

    override fun onStartScreen() {

    }

    override fun onEndScreen() {}

    fun unpauseGame() {
        print("Unpause game\n")
        app!!.stateManager.detach(this)

        val gameRunningState = this.app!!.stateManager.getState(GameRunningState::class.java)
        gameRunningState.isEnabled = true
    }

    fun exitGame() {
        app!!.stop()
    }
}