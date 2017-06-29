package com.shabb.pongfinal.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.shabb.pongfinal.objects.Ball;
import com.shabb.pongfinal.objects.Floor;
import com.shabb.pongfinal.objects.Paddle;
import com.shabb.pongfinal.objects.Stage1;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.loaderv2.types.RegisterEffectType;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;

import javax.annotation.Nonnull;

public class GameRunningState extends AbstractAppState implements ScreenController {
    private final AudioRenderer audioRenderer;
    private final ViewPort guiViewPort;
    private Node rootNode;
    private final ViewPort viewPort;
    private final Node guiNode;
    private final AssetManager assetManager;
    private final FlyByCamera flyCam;
    private final Camera cam;
    private Node localRootNode = new Node("Game Screen RootNode");
    private Node localGuiNode = new Node("Game Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Blue;
    private InputManager inputManager;
    private boolean isRunning;
    private SimpleApplication app;
    private Paddle paddle1;
    private Paddle paddle2;
    private Floor floor;
    private Ball ball;
    private Floor wall1;
    private Floor wall2;
    private Floor wall3;
    private Floor wall4;
    private AudioNode backgroundMusic;
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private Stage1 stage1;

    public GameRunningState(SimpleApplication app){
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.audioRenderer = app.getAudioRenderer();
        this.inputManager = app.getInputManager();
        this.assetManager = app.getAssetManager();
        this.flyCam = app.getFlyByCamera();
        this.cam = app.getCamera();
        this.guiViewPort = app.getGuiViewPort();

//        jphp.runtime;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;

        /** Load this scene */
        viewPort.setBackgroundColor(backgroundColor);

        ((SimpleApplication) app).setDisplayStatView(false);
        flyCam.setEnabled(false);

        cam.setLocation(new Vector3f(0, 100, 0));
//        cam.setLocation(new Vector3f(0, 0, 150));
        cam.setParallelProjection(true);

        float aspect = (float) cam.getWidth() / cam.getHeight();

        float frustumSize = 100;
        cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);

//        cam.setLocation(new Vector3f(50, 50, 150));

        // Must add a light to make the lit object visible!
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,-5,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Drop shadows
        final int SHADOWMAP_SIZE = 1024;
        DirectionalLightShadowRenderer dlsr;
        DirectionalLightShadowFilter dlsf;

        dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
//        dlsr.setEdgesThickness(50);
//        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        viewPort.addProcessor(dlsr);

        dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(sun);
        dlsf.setShadowIntensity(0.10f);
        dlsf.setEnabled(true);

        // Ambiance Occlusion
        SSAOFilter ssaoFilter = new SSAOFilter();

        // Antialiasing
        FXAAFilter fxaaFilter = new FXAAFilter();
        fxaaFilter.setSubPixelShift(0.0f);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
//        fpp.addFilter(ssaoFilter);
        fpp.addFilter(fxaaFilter);
        viewPort.addProcessor(fpp);



        stage1 = new Stage1();
        stage1.initGraphics(assetManager, rootNode);

        paddle1 = new Paddle("Player 1", stage1.getPlayer1());
        paddle2 = new Paddle("Player 2", stage1.getPlayer2());
        floor = new Floor(stage1.getFloor());
        wall1 = new Floor(stage1.getWall1());
        wall2 = new Floor(stage1.getWall2());
        wall3 = new Floor(stage1.getWall3());
        wall4 = new Floor(stage1.getWall4());
        ball = new Ball(stage1.getBall());

        paddle1.initGraphics(assetManager, rootNode);
        paddle2.initGraphics(assetManager, rootNode);
        ball.initGraphics(assetManager, rootNode);
        floor.initGraphics(assetManager, rootNode);
        wall1.initGraphics(assetManager, rootNode);
        wall2.initGraphics(assetManager, rootNode);
        wall3.initGraphics(assetManager, rootNode);
        wall4.initGraphics(assetManager, rootNode);

        cam.lookAt(new Vector3f(0,0,0), new Vector3f(0, 1.0f, 0));

        initKeys();

        initGui();

        initAudio();
    }

    private void initPhysics() {
        BulletAppState bulletAppState = this.app.getStateManager().getState(BulletAppState.class);
        bulletAppState.setDebugEnabled(true);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, 0));

        ball.initPhysics(bulletAppState);
        paddle1.initPhysics(bulletAppState);
        paddle2.initPhysics(bulletAppState);
        floor.initPhysics(bulletAppState);
        wall1.initPhysics(bulletAppState);
        wall2.initPhysics(bulletAppState);
        wall3.initPhysics(bulletAppState);
        wall4.initPhysics(bulletAppState);
    }

    private void initGui() {
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.registerEffect(new RegisterEffectType("imageSizeFade", "com.shabb.pongfinal.effects.ImageSizeHide"));
        nifty.fromXml("Interface/GameInterface.xml", "GameInterface", this);
        guiViewPort.addProcessor(niftyDisplay);

        Screen screen = nifty.getScreen("GameInterface");
        if (screen == null) {
            return;
        }

        Element startImage1;
        Element startImage2;
        Element startImage3;

        startImage1 = screen.findElementById("start-1");
        startImage2 = screen.findElementById("start-2");
        startImage3 = screen.findElementById("start-3");

        if (startImage1 == null || startImage2 == null || startImage3 == null) {
            System.out.println("Cannot find a start images :(");
            return;
        }

        startImage2.setVisible(false);
        startImage3.setVisible(false);

        DeferredManager dm = new DefaultDeferredManager();
        Deferred<Boolean, Long, String> startImage1EffectDeferred = new DeferredObject<>();
        Promise startImage1EffectPromise = startImage1EffectDeferred.promise();

        Deferred<Boolean, Long, String> startImage2EffectDeferred = new DeferredObject();
        Promise startImage2EffectPromise = startImage2EffectDeferred.promise();

        Deferred<Boolean, Long, String> startImage3EffectDeferred = new DeferredObject();
        Promise startImage3EffectPromise = startImage3EffectDeferred.promise();

        startImage1.startEffect(EffectEventId.onCustom, () -> {
            startImage1EffectDeferred.resolve(true);
        }, "onFadeSizeIn");

        startImage1EffectPromise.done((result) -> {
            System.out.println("Done with Effect Image 1!");
            startImage1.setVisible(false);
            startImage2.setVisible(true);

            startImage2.startEffect(EffectEventId.onCustom, () -> {
                startImage2EffectDeferred.resolve(true);
            }, "onFadeSizeIn");
        });

        startImage2EffectPromise.done((result) -> {
            System.out.println("Done with Effect Image 2!");
            startImage2.setVisible(false);
            startImage3.setVisible(true);

            startImage3.startEffect(EffectEventId.onCustom, () -> {
                startImage3EffectDeferred.resolve(true);
            }, "onFadeSizeIn");
        });

        dm.when(startImage1EffectPromise, startImage2EffectPromise, startImage3EffectPromise)
                .done(result -> {
                    System.out.println("Start Game!");
                    initPhysics();
                });


    }

    private void initAudio() {
        //-- Setup Audio
        backgroundMusic = new AudioNode(assetManager, "Sounds/sphere.ogg", AudioData.DataType.Stream);
        backgroundMusic.setLooping(true);
        backgroundMusic.setPositional(false);
        backgroundMusic.setVolume(1);
        rootNode.attachChild(backgroundMusic);
        backgroundMusic.play();
    }


    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);

        backgroundMusic.stop();
        audioRenderer.deleteAudioData(backgroundMusic.getAudioData());

        super.cleanup();
    }

    /**
     * Update is only called when the game state is enabled.
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        BulletAppState bulletAppState = this.app.getStateManager().getState(BulletAppState.class);

        this.app.setDisplayFps(true);
        paddle1.update(tpf, bulletAppState);
        paddle2.update(tpf, bulletAppState);
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        viewPort.setBackgroundColor(backgroundColor);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);

    }

    private void initKeys() {
        inputManager.addMapping("Player 1 - Up",   new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Player 1 - Down",  new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Script",  new KeyTrigger(KeyInput.KEY_C));
        inputManager.addListener(actionListener,"Pause", "Script");
        inputManager.addListener(analogListener,"Player 1 - Up", "Player 1 - Down");
    }

    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed && isEnabled()) {
                //-- Don't pause the game is someone is typing in the script state
                ScriptScreenState scriptScreenState = app.getStateManager().getState(ScriptScreenState.class);
                if (scriptScreenState != null) {
                    System.out.println("Do not allow pausing!");
                    return;
                }

                setEnabled(false);

                PauseScreenState pauseScreenState = new PauseScreenState(app);
                app.getStateManager().attach(pauseScreenState);
                System.out.print("Pause game\n");
            }
            else if (name.equals("Script") && !keyPressed) {
                ScriptScreenState scriptScreenState = app.getStateManager().getState(ScriptScreenState.class);

                //-- If there is no ScriptScreenState then create one
                if (scriptScreenState == null) {
                    scriptScreenState = new ScriptScreenState(app);
                    app.getStateManager().attach(scriptScreenState);
                }

                //-- Toggle visibility of state
                if (scriptScreenState.isEnabled()) {
                    scriptScreenState.setEnabled(false);
                } else {
                    scriptScreenState.setEnabled(true);
                }
            }
        }
    };

    private AnalogListener analogListener = (String name, float value, float tpf) -> {
        if (!isEnabled()) {
            return;
        }

        //-- Don't send input if the script window is up
        ScriptScreenState scriptScreenState = app.getStateManager().getState(ScriptScreenState.class);
        if (scriptScreenState != null) {
            System.out.println("Do not allow input!");
            return;
        }

        float speed = 10.0f;

        if (name.equals("Player 1 - Up")) {
            paddle1.addLocalTranslation(0.0f, 0.0f, value * speed);
        }
        else if (name.equals("Player 1 - Down")) {
            paddle1.addLocalTranslation(0.0f, 0.0f, -1 * value * speed);
        }
    };

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }
}
