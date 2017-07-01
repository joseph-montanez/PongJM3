package com.shabb.pongfinal.states

import com.jme3.app.Application
import com.jme3.app.SimpleApplication
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.asset.AssetManager
import com.jme3.audio.AudioData
import com.jme3.audio.AudioNode
import com.jme3.audio.AudioRenderer
import com.jme3.bullet.BulletAppState
import com.jme3.input.FlyByCamera
import com.jme3.input.InputManager
import com.jme3.input.KeyInput
import com.jme3.input.controls.ActionListener
import com.jme3.input.controls.AnalogListener
import com.jme3.input.controls.KeyTrigger
import com.jme3.light.DirectionalLight
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector3f
import com.jme3.niftygui.NiftyJmeDisplay
import com.jme3.post.FilterPostProcessor
import com.jme3.post.filters.FXAAFilter
import com.jme3.post.ssao.SSAOFilter
import com.jme3.renderer.Camera
import com.jme3.renderer.ViewPort
import com.jme3.scene.Node
import com.jme3.shadow.DirectionalLightShadowFilter
import com.jme3.shadow.DirectionalLightShadowRenderer
import com.shabb.pongfinal.objects.*
import de.lessvoid.nifty.Nifty
import de.lessvoid.nifty.effects.EffectEventId
import de.lessvoid.nifty.elements.Element
import de.lessvoid.nifty.loaderv2.types.RegisterEffectType
import de.lessvoid.nifty.screen.Screen
import de.lessvoid.nifty.screen.ScreenController
import org.jdeferred.Deferred
import org.jdeferred.DeferredManager
import org.jdeferred.Promise
import org.jdeferred.impl.DefaultDeferredManager
import org.jdeferred.impl.DeferredObject

class GameRunningState(app: SimpleApplication) : AbstractAppState(), ScreenController {
    private val audioRenderer: AudioRenderer
    private val guiViewPort: ViewPort
    private val rootNode: Node
    private val viewPort: ViewPort
    private val guiNode: Node
    private val assetManager: AssetManager
    private val flyCam: FlyByCamera
    private val cam: Camera
    private val localRootNode = Node("Game Screen RootNode")
    private val localGuiNode = Node("Game Screen GuiNode")
    private val backgroundColor = ColorRGBA.Blue
    private val inputManager: InputManager
    private var app: SimpleApplication? = null
    private var paddle1: Paddle? = null
    private var paddle2: Paddle? = null
    private var floor: Floor? = null
    private var ball: Ball? = null
    private var wall1: Floor? = null
    private var wall2: Floor? = null
    private var wall3: Floor? = null
    private var wall4: Floor? = null
    private var backgroundMusic: AudioNode? = null
    private var niftyDisplay: NiftyJmeDisplay? = null
    private var nifty: Nifty? = null
    private var stage1: Stage1? = null

    init {
        this.rootNode = app.rootNode
        this.viewPort = app.viewPort
        this.guiNode = app.guiNode
        this.audioRenderer = app.audioRenderer
        this.inputManager = app.inputManager
        this.assetManager = app.assetManager
        this.flyCam = app.flyByCamera
        this.cam = app.camera
        this.guiViewPort = app.guiViewPort

        //        jphp.runtime;
    }

    override fun initialize(stateManager: AppStateManager?, app: Application?) {
        super.initialize(stateManager, app)
        this.app = app as SimpleApplication?

        // Load this scene
        viewPort.backgroundColor = backgroundColor

        (app as SimpleApplication).setDisplayStatView(false)
        flyCam.isEnabled = false

        cam.location = Vector3f(0f, 100f, 0f)
        //        cam.setLocation(new Vector3f(0, 0, 150));
        cam.isParallelProjection = true

        val aspect = cam.width.toFloat() / cam.height

        val frustumSize = 100f
        cam.setFrustum(-1000f, 1000f, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize)

        //        cam.setLocation(new Vector3f(50, 50, 150));

        // Must add a light to make the lit object visible!
        val sun = DirectionalLight()
        sun.direction = Vector3f(1f, -5f, -2f).normalizeLocal()
        sun.color = ColorRGBA.White
        rootNode.addLight(sun)

        // Drop shadows
        val SHADOWMAP_SIZE = 1024
        val dlsr: DirectionalLightShadowRenderer
        val dlsf: DirectionalLightShadowFilter

        dlsr = DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3)
        dlsr.light = sun
        //        dlsr.setEdgesThickness(50);
        //        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        viewPort.addProcessor(dlsr)

        dlsf = DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3)
        dlsf.light = sun
        dlsf.shadowIntensity = 0.10f
        dlsf.isEnabled = true

        // Ambiance Occlusion
        val ssaoFilter = SSAOFilter()

        // Antialiasing
        val fxaaFilter = FXAAFilter()
        fxaaFilter.subPixelShift = 0.0f

        val fpp = FilterPostProcessor(assetManager)
        fpp.addFilter(dlsf)
        //        fpp.addFilter(ssaoFilter);
        fpp.addFilter(fxaaFilter)
        viewPort.addProcessor(fpp)



        stage1 = Stage1()
        stage1!!.initGraphics(assetManager, rootNode)

        paddle1 = Paddle("Player 1", stage1!!.player1!!)
        paddle2 = Paddle("Player 2", stage1!!.player2!!)
        floor = Floor(stage1!!.floor!!)
        wall1 = Wall(stage1!!.wall1!!)
        wall2 = Wall(stage1!!.wall2!!)
        wall3 = Wall(stage1!!.wall3!!)
        wall4 = Wall(stage1!!.wall4!!)
        ball = Ball(stage1!!.ball!!)

        paddle1!!.initGraphics(assetManager, rootNode)
        paddle2!!.initGraphics(assetManager, rootNode)
        ball!!.initGraphics(assetManager, rootNode)
        floor!!.initGraphics(assetManager, rootNode)
        wall1!!.initGraphics(assetManager, rootNode)
        wall2!!.initGraphics(assetManager, rootNode)
        wall3!!.initGraphics(assetManager, rootNode)
        wall4!!.initGraphics(assetManager, rootNode)

        cam.lookAt(Vector3f(0f, 0f, 0f), Vector3f(0f, 1.0f, 0f))

        initKeys()

        initGui()

        initAudio()
    }

    private fun initPhysics() {
        val bulletAppState = this.app!!.stateManager.getState(BulletAppState::class.java)
        bulletAppState.isDebugEnabled = true
        bulletAppState.physicsSpace.setGravity(Vector3f(0f, 0f, 0f))

        ball!!.initPhysics(bulletAppState)
        paddle1!!.initPhysics(bulletAppState)
        paddle2!!.initPhysics(bulletAppState)
        floor!!.initPhysics(bulletAppState)
        wall1!!.initPhysics(bulletAppState)
        wall2!!.initPhysics(bulletAppState)
        wall3!!.initPhysics(bulletAppState)
        wall4!!.initPhysics(bulletAppState)
    }

    private fun initGui() {
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort)
        nifty = niftyDisplay!!.nifty
        nifty!!.registerEffect(RegisterEffectType("imageSizeFade", "com.shabb.pongfinal.effects.ImageSizeHide"))
        nifty!!.fromXml("Interface/GameInterface.xml", "GameInterface", this)
        guiViewPort.addProcessor(niftyDisplay!!)

        val screen = nifty!!.getScreen("GameInterface") ?: return

        val startImage1: Element?
        val startImage2: Element?
        val startImage3: Element?

        startImage1 = screen.findElementById("start-1")
        startImage2 = screen.findElementById("start-2")
        startImage3 = screen.findElementById("start-3")

        if (startImage1 == null || startImage2 == null || startImage3 == null) {
            println("Cannot find a start images :(")
            return
        }

        startImage2.isVisible = false
        startImage3.isVisible = false

        val dm = DefaultDeferredManager()
        val startImage1EffectDeferred = DeferredObject<Boolean, Long, String>()
        val startImage1EffectPromise = startImage1EffectDeferred.promise()

        val startImage2EffectDeferred = DeferredObject<Boolean, Long, String>()
        val startImage2EffectPromise = startImage2EffectDeferred.promise()

        val startImage3EffectDeferred = DeferredObject<Boolean, Long, String>()
        val startImage3EffectPromise = startImage3EffectDeferred.promise()

        startImage1.startEffect(EffectEventId.onCustom, { startImage1EffectDeferred.resolve(true) }, "onFadeSizeIn")

        startImage1EffectPromise.done { _ ->
            println("Done with Effect Image 1!")
            startImage1.isVisible = false
            startImage2.isVisible = true

            startImage2.startEffect(EffectEventId.onCustom, { startImage2EffectDeferred.resolve(true) }, "onFadeSizeIn")
        }

        startImage2EffectPromise.done({ _ ->
            println("Done with Effect Image 2!")
            startImage2.isVisible = false
            startImage3.isVisible = true

            startImage3.startEffect(EffectEventId.onCustom, { startImage3EffectDeferred.resolve(true) }, "onFadeSizeIn")
        })

        dm.`when`(startImage1EffectPromise, startImage2EffectPromise, startImage3EffectPromise)
                .done { _ ->
                    println("Start Game!")
                    initPhysics()
                }


    }

    private fun initAudio() {
        //-- Setup Audio
        backgroundMusic = AudioNode(assetManager, "Sounds/sphere.ogg", AudioData.DataType.Stream)
        backgroundMusic!!.isLooping = true
        backgroundMusic!!.isPositional = false
        backgroundMusic!!.volume = 1f
        rootNode.attachChild(backgroundMusic)
        backgroundMusic!!.play()
    }


    override fun cleanup() {
        rootNode.detachChild(localRootNode)
        guiNode.detachChild(localGuiNode)

        backgroundMusic!!.stop()
        audioRenderer.deleteAudioData(backgroundMusic!!.audioData)

        super.cleanup()
    }

    /**
     * Update is only called when the game state is enabled.

     * @param tpf Time
     */
    override fun update(tpf: Float) {
        val bulletAppState = this.app!!.stateManager.getState(BulletAppState::class.java)

        this.app!!.setDisplayFps(true)
        paddle1!!.update(tpf, bulletAppState)
        paddle2!!.update(tpf, bulletAppState)
    }

    override fun stateAttached(stateManager: AppStateManager?) {
        rootNode.attachChild(localRootNode)
        guiNode.attachChild(localGuiNode)
        viewPort.backgroundColor = backgroundColor
    }

    override fun stateDetached(stateManager: AppStateManager?) {
        rootNode.detachChild(localRootNode)
        guiNode.detachChild(localGuiNode)

    }

    private fun initKeys() {
        inputManager.addMapping("Player 1 - Up", KeyTrigger(KeyInput.KEY_W))
        inputManager.addMapping("Player 1 - Down", KeyTrigger(KeyInput.KEY_S))
        inputManager.addMapping("Pause", KeyTrigger(KeyInput.KEY_P))
        inputManager.addMapping("Script", KeyTrigger(KeyInput.KEY_C))
        inputManager.addListener(actionListener, "Pause", "Script")
        inputManager.addListener(analogListener, "Player 1 - Up", "Player 1 - Down")
    }

    private val actionListener = ActionListener { name, keyPressed, tpf ->
        if (name == "Pause" && !keyPressed && isEnabled) {
            //-- Don't pause the game is someone is typing in the script state
            val scriptScreenState = app.stateManager.getState(ScriptScreenState::class.java)
            if (scriptScreenState != null) {
                println("Do not allow pausing!")
                return@ActionListener
            }

            isEnabled = false

            val pauseScreenState = PauseScreenState(app)
            app.stateManager.attach(pauseScreenState)
            print("Pause game\n")
        } else if (name == "Script" && !keyPressed) {
            var scriptScreenState: ScriptScreenState? = app.stateManager.getState(ScriptScreenState::class.java)

            //-- If there is no ScriptScreenState then create one
            if (scriptScreenState == null) {
                scriptScreenState = ScriptScreenState(app)
                app.stateManager.attach(scriptScreenState)
            }

            //-- Toggle visibility of state
            scriptScreenState.isEnabled = !scriptScreenState.isEnabled
        }
    }

    private val analogListener = AnalogListener { name, value, tpf ->
        if (isEnabled) {
            //-- Don't send input if the script window is up
            val scriptScreenState = app.stateManager.getState(ScriptScreenState::class.java)
            if (scriptScreenState == null) {
                val speed = 10.0f

                if (name == "Player 1 - Up") {
                    paddle1!!.addLocalTranslation(0.0f, 0.0f, value * speed)
                } else if (name == "Player 1 - Down") {
                    paddle1!!.addLocalTranslation(0.0f, 0.0f, -1f * value * speed)
                }
            }
        }
    }

    override fun bind(nifty: Nifty, screen: Screen) {

    }

    override fun onStartScreen() {

    }

    override fun onEndScreen() {

    }
}
