package com.shabb.pongfinal.states

import com.jme3.app.Application
import com.jme3.app.SimpleApplication
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.asset.AssetManager
import com.jme3.audio.AudioRenderer
import com.jme3.input.InputManager
import com.jme3.input.KeyInput
import com.jme3.input.controls.ActionListener
import com.jme3.input.controls.KeyTrigger
import com.jme3.math.ColorRGBA
import com.jme3.niftygui.NiftyJmeDisplay
import com.jme3.renderer.ViewPort
import com.jme3.scene.Node
import com.shabb.pongfinal.Main
import de.lessvoid.nifty.Nifty
import de.lessvoid.nifty.controls.Console
import de.lessvoid.nifty.controls.ConsoleCommands
import de.lessvoid.nifty.screen.Screen
import de.lessvoid.nifty.screen.ScreenController
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.HashMap
import java.util.Optional

class ScriptScreenState(app: SimpleApplication) : AbstractAppState(), ScreenController {

    private val guiViewPort: ViewPort
    private val inputManager: InputManager
    private val audioRenderer: AudioRenderer
    private var app: Main? = null
    private val viewPort: ViewPort
    private val rootNode: Node
    private val guiNode: Node
    private val assetManager: AssetManager
    private val localRootNode = Node("Start Screen RootNode")
    private val localGuiNode = Node("Start Screen GuiNode")
    private val backgroundColor = ColorRGBA.Gray

    private var niftyDisplay: NiftyJmeDisplay? = null
    private var nifty: Nifty? = null

    private val pressedKeys = HashMap<String, Boolean>()

    private val screen: Screen?
        get() {
            val screen: Screen? = nifty!!.getScreen("ScriptScreen")
            if (screen == null) {
                println("Cannot find screen: ScriptScreen")
                return null
            }
            return screen
        }

    private val console: Console?
        get() {
            val console: Console? = screen!!.findNiftyControl("console", Console::class.java)

            if (console == null) {
                println("Cannot find control: console")
                return null
            }

            return console
        }

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
        this.app = app as Main?

        rootNode.attachChild(localRootNode)
        guiNode.attachChild(localGuiNode)
        viewPort.backgroundColor = backgroundColor

        /** init the screen  */
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort)
        nifty = niftyDisplay!!.nifty
        nifty!!.fromXml("Interface/ScriptInterface.xml", "ScriptScreen", this)
        guiViewPort.addProcessor(niftyDisplay!!)

        val consoleCommands: ConsoleCommands
        if (console != null) {
            consoleCommands = ConsoleCommands(nifty!!, console!!)
            consoleCommands.registerCommand("kts", KotlinCommand())
            consoleCommands.registerCommand("script", ScriptCommand())
            consoleCommands.enableCommandCompletion(true)
        }

        initKeys()
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

    fun initKeys() {
        inputManager.addMapping("T", KeyTrigger(KeyInput.KEY_T))
        inputManager.addMapping("CTRL", KeyTrigger(KeyInput.KEY_LCONTROL))
        inputManager.addListener(actionListener, "T", "CTRL")
    }

    private val actionListener = ActionListener { name, keyPressed, tpf ->

        if (keyPressed) {
            pressedKeys.put(name, true)
        } else {
            pressedKeys.put(name, false)
        }

        println(pressedKeys)

        val tPressed = pressedKeys.containsKey("T") && pressedKeys["T"] as Boolean
        val ctrlPressed = pressedKeys.containsKey("CTRL") && pressedKeys["CTRL"] as Boolean

        if (tPressed && ctrlPressed) {
            close()
        }
    }

    fun close() {
        app!!.stateManager.detach(this)
    }

    fun writeToConsole(result: String) {
        val console = console
        console?.output(result)
    }

    private inner class KotlinCommand : ConsoleCommands.ConsoleCommand {
        override fun execute(args: Array<String>) {
            val content = StringBuilder()
            if (args.size > 1) {
                for (i in 1..args.size - 1) {
                    val a = args[i]
                    content.append(a)
                }
            }
            app!!.scriptCode = Optional.of(content.toString())
        }
    }

    private inner class ScriptCommand : ConsoleCommands.ConsoleCommand {
        override fun execute(args: Array<String>) {
            val content = StringBuilder()
            if (args.size > 1) {
                (1..args.size - 1)
                        .map { args[it] }
                        .forEach { content.append(it) }
            }
            val filename = content.toString()
            try {
                val code = String(Files.readAllBytes(Paths.get("assets/Scripts/" + filename)))
                app!!.scriptCode = Optional.of(code)
            } catch (e: IOException) {
                writeToConsole(String.format("File does not exist: %s", e.message))
            }

        }
    }
}