package com.shabb.pongfinal;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.shabb.pongfinal.states.ScriptScreenState;
import com.shabb.pongfinal.states.StartScreenState;
import org.develnext.jphp.core.compiler.jvm.JvmCompiler;
import org.develnext.jphp.core.syntax.SyntaxAnalyzer;
import org.develnext.jphp.core.tokenizer.Tokenizer;
import php.runtime.common.LangMode;
import php.runtime.env.CompileScope;
import php.runtime.env.Context;
import php.runtime.env.Environment;
import php.runtime.ext.CoreExtension;
import php.runtime.ext.SPLExtension;
import php.runtime.reflection.ModuleEntity;

import javax.script.*;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
//import org.develnext.jphp.core.compiler.jvm;

//import com.shabb.screens.StartScreenState;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    private boolean isRunning = true;
    Geometry box;
    StartScreenState startScreenState;
    BulletAppState bulletAppState;
    //-- JavaScript
    public Optional<String> javaScriptCode;
    ScriptEngine javaScriptEngine;
    SimpleBindings javaScriptGlobal;
    StringWriter javaScriptWriter;
    //-- PHP
    public Optional<String> phpCode;
    ScriptEngine phpEngine;
    SimpleBindings phpGlobal;
    StringWriter phpWriter;
    Environment phpEnvironment;


    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Pong");
        settings.setVSync(true);
//        settings.setFullscreen(true);
        settings.setFrameRate(60); // set to less than or equal screen refresh rate
//        settings.setSamples(2);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initJavaScriptEngine();
        loadJS();

        initPhpEngine();
        loadPhp();



        startScreenState = new StartScreenState(this);
        stateManager.attach(startScreenState);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (javaScriptCode.isPresent()) {
            try {
                javaScriptEngine.eval(javaScriptCode.get(), javaScriptGlobal);
                ScriptScreenState scriptScreenState = getStateManager().getState(ScriptScreenState.class);
                if (scriptScreenState != null) {
                    scriptScreenState.writeToConsole("> " + javaScriptWriter.toString());
                }
            } catch (ScriptException e) {
                ScriptScreenState scriptScreenState = getStateManager().getState(ScriptScreenState.class);
                if (scriptScreenState != null) {
                    scriptScreenState.writeToConsole("JavaScript runtime error: " + e.getMessage());
                }
            }

            javaScriptCode = Optional.empty();
        }

        if (phpCode.isPresent()) {
            try {
                phpEngine.eval(phpCode.get(), phpGlobal);
                ScriptScreenState scriptScreenState = getStateManager().getState(ScriptScreenState.class);
                if (scriptScreenState != null) {
                    scriptScreenState.writeToConsole("> " + phpWriter.toString());
                }
            } catch (ScriptException e) {
                ScriptScreenState scriptScreenState = getStateManager().getState(ScriptScreenState.class);
                if (scriptScreenState != null) {
                    scriptScreenState.writeToConsole("PHP runtime error: " + e.getMessage());
                }
            }

            phpCode = Optional.empty();
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public ScriptEngine getScriptEngine() {
        return javaScriptEngine;
    }

    public void setScriptEngine(ScriptEngine engine) {
        this.javaScriptEngine = engine;
    }

    public void initJavaScriptEngine() {
        javaScriptCode = Optional.empty();
        javaScriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        javaScriptWriter = new StringWriter();
        javaScriptEngine.getContext().setWriter(javaScriptWriter);
        javaScriptEngine.put("app", this);
    }

    public void initPhpEngine() {
        phpCode = Optional.empty();
        phpEngine = new ScriptEngineManager().getEngineByName("jphp");
        phpWriter = new StringWriter();
        phpEngine.getContext().setWriter(phpWriter);
        phpEngine.put("app", this);
    }

    public void loadJS() {
        try {
            String code = new String(Files.readAllBytes(Paths.get("assets/Scripts/JavaScript/built-in.js")));
            CompiledScript compiled = ((Compilable) javaScriptEngine).compile(code);
            Object builtin = compiled.eval();
            javaScriptGlobal = new SimpleBindings();
            javaScriptGlobal.put("builtin", builtin);
        } catch (IOException e) {
            System.out.println("File does not exist: " + e.getMessage());
        } catch (ScriptException e) {
            System.out.println("JavaScript compile error: " + e.getMessage());
        }
    }

    public void loadPhp() {
        try {
            String code = new String(Files.readAllBytes(Paths.get("assets/Scripts/PHP/built-in.php")));
            CompiledScript compiled = ((Compilable) phpEngine).compile(code);
            Object builtin = compiled.eval();
            phpGlobal = new SimpleBindings();
            phpGlobal.put("builtin", builtin);
        } catch (IOException e) {
            System.out.println("File does not exist: " + e.getMessage());
        } catch (ScriptException e) {
            System.out.println("PHP compile error: " + e.getMessage());
        }
    }

    public void setUpPhp() {

        CompileScope compileScope = new CompileScope();
        compileScope.setDebugMode(true);
        compileScope.setLangMode(LangMode.DEFAULT);

        compileScope.registerExtension(new CoreExtension());
        compileScope.registerExtension(new SPLExtension());

        phpEnvironment = new Environment(compileScope);
        Context context = new Context("<?php echo 'hello world!';");


        JvmCompiler compiler = new JvmCompiler(phpEnvironment, context, getPhpSyntax(context));
        ModuleEntity module = compiler.compile();
        phpEnvironment.getScope().loadModule(module);

        try {
//            module.
            phpEnvironment.registerModule(module);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

    }

    protected SyntaxAnalyzer getPhpSyntax(Context context){
        Tokenizer tokenizer = null;
        try {
            tokenizer = new Tokenizer(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        phpEnvironment.scope.setLangMode(LangMode.DEFAULT);
        return new SyntaxAnalyzer(phpEnvironment, tokenizer);
    }
}
