package com.shabb.pongfinal.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;

public class StartScreenState extends AbstractAppState implements ScreenController {

    private final ViewPort guiViewPort;
    private final InputManager inputManager;
    private final AudioRenderer audioRenderer;
    private SimpleApplication app;
    private ViewPort viewPort;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private Node localRootNode = new Node("Start Screen RootNode");
    private Node localGuiNode = new Node("Start Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Gray;

    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private AudioNode backgroundMusic;

    public StartScreenState(SimpleApplication app) {
        this.rootNode = app.getRootNode();
        this.viewPort = app.getViewPort();
        this.guiNode = app.getGuiNode();
        this.inputManager = app.getInputManager();
        this.audioRenderer = app.getAudioRenderer();
        this.assetManager = app.getAssetManager();
        this.guiViewPort = app.getGuiViewPort();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;

        //-- Disable a few things!
        this.app.setDisplayFps(false);
        this.app.setDisplayStatView(false);
        this.app.getFlyByCamera().setEnabled(false);

        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        viewPort.setBackgroundColor(backgroundColor);

        /** init the screen */
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/MainMenu.xml", "GScreen0", this);
        guiViewPort.addProcessor(niftyDisplay);

        //-- Setup Audio
        backgroundMusic = new AudioNode(assetManager, "Sounds/forward-the-feeling.ogg", DataType.Stream);
        backgroundMusic.setLooping(true);
        backgroundMusic.setPositional(false);
        backgroundMusic.setVolume(0.25f);
        rootNode.attachChild(backgroundMusic);
        backgroundMusic.play();
    }

    @Override
    public void update(float tpf) {
        /** any main loop action happens here */
        nifty.update();
    }

    @Override
    public void cleanup() {
        rootNode.detachChild(localRootNode);
        guiNode.detachChild(localGuiNode);

        niftyDisplay.cleanup();
        nifty.exit();

        backgroundMusic.stop();
        audioRenderer.deleteAudioData(backgroundMusic.getAudioData());

        super.cleanup();
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {

    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {
    }

    public void startGame() {
        System.out.print("Start Game!");
        app.getStateManager().detach(this);

        GameRunningState gameRunningState = new GameRunningState(app);
        app.getStateManager().attach(gameRunningState);
    }

    public void exitGame() {
        app.stop();
    }
}