package com.shabb.pongfinal.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.shabb.pongfinal.Main;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.ConsoleCommands;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

public class ScriptScreenState extends AbstractAppState implements ScreenController {

    private final ViewPort guiViewPort;
    private final InputManager inputManager;
    private final AudioRenderer audioRenderer;
    private Main app;
    private ViewPort viewPort;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private Node localRootNode = new Node("Start Screen RootNode");
    private Node localGuiNode = new Node("Start Screen GuiNode");
    private final ColorRGBA backgroundColor = ColorRGBA.Gray;

    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;

    private HashMap<String, Boolean> pressedKeys = new HashMap<String, Boolean>();

    public ScriptScreenState(SimpleApplication app) {
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
        this.app = (Main) app;

        rootNode.attachChild(localRootNode);
        guiNode.attachChild(localGuiNode);
        viewPort.setBackgroundColor(backgroundColor);

        /** init the screen */
        niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/ScriptInterface.xml", "ScriptScreen", this);
        guiViewPort.addProcessor(niftyDisplay);

        Console console;
        ConsoleCommands consoleCommands;
        console = getConsole();
        if (console != null) {
            consoleCommands = new ConsoleCommands(nifty, console);
            consoleCommands.registerCommand("js", new JsCommand());
            consoleCommands.registerCommand("php", new PhpCommand());
            consoleCommands.registerCommand("script", new ScriptCommand());
            consoleCommands.enableCommandCompletion(true);
        }

        initKeys();
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

    public void initKeys() {
        inputManager.addMapping("T", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("CTRL", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addListener(actionListener,"T", "CTRL");
    }

    private ActionListener actionListener = (name, keyPressed, tpf) -> {

        if (keyPressed) {
            pressedKeys.put(name, true);
        } else {
            pressedKeys.put(name, false);
        }

        System.out.println(pressedKeys);

        Boolean tPressed = pressedKeys.containsKey("T") && pressedKeys.get("T");
        Boolean ctrlPressed = pressedKeys.containsKey("CTRL") && pressedKeys.get("CTRL");

        if (tPressed && ctrlPressed) {
            close();
        }
    };

    public void close() {
        app.getStateManager().detach(this);
    }

    public void writeToConsole(String result) {
        Console console = getConsole();
        if (console != null) {
            console.output(result);
        }
    }

    @Nullable
    private Console getConsole() {
        Screen screen;
        Console console;

        screen = getScreen();
        if (screen == null) {
            return null;
        }

        console = screen.findNiftyControl("console", Console.class);
        if (console == null) {
            System.out.println("Cannot find control: console");
            return null;
        }

        return console;
    }

    private Screen getScreen() {
        Screen screen;
        screen = nifty.getScreen("ScriptScreen");
        if (screen == null) {
            System.out.println("Cannot find screen: ScriptScreen");
            return null;
        }
        return screen;
    }

    private class JsCommand implements ConsoleCommands.ConsoleCommand {
        @Override
        public void execute(final String[] args) {
            StringBuilder content = new StringBuilder();
            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    String a = args[i];
                    content.append(a);
                }
            }
            app.javaScriptCode = Optional.of(content.toString());
        }
    }

    private class PhpCommand implements ConsoleCommands.ConsoleCommand {
        @Override
        public void execute(final String[] args) {
            StringBuilder content = new StringBuilder();
            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    String a = args[i];
                    content.append(a);
                }
            }
            app.phpCode = Optional.of(content.toString());
        }
    }

    private class ScriptCommand implements ConsoleCommands.ConsoleCommand {
        @Override
        public void execute(final String[] args) {
            StringBuilder content = new StringBuilder();
            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    String a = args[i];
                    content.append(a);
                }
            }
            String filename = content.toString();
            try {
                String code = new String(Files.readAllBytes(Paths.get("assets/Scripts/" + filename)));
                app.javaScriptCode = Optional.of(code);
            } catch (IOException e) {
                writeToConsole(String.format("File does not exist: %s", e.getMessage()));
            }

        }
    }
}