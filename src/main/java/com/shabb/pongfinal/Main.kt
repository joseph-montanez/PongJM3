package com.shabb.pongfinal

import com.jme3.app.SimpleApplication
import com.jme3.bullet.BulletAppState
import com.jme3.renderer.RenderManager
import com.jme3.scene.Geometry
import com.jme3.system.AppSettings
import com.shabb.pongfinal.states.ScriptScreenState
import com.shabb.pongfinal.states.StartScreenState
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine

import javax.script.*
import java.io.IOException
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.time.Instant
import java.util.Optional

class Main : SimpleApplication() {
    internal var box: Geometry? = null
    internal lateinit var startScreenState: StartScreenState
    internal lateinit var bulletAppState: BulletAppState
    //-- JavaScript
    var scriptCode: Optional<String> = Optional.empty<String>()
    internal lateinit var scriptEngine: KotlinJsr223JvmLocalScriptEngine
    internal lateinit var scriptGlobal: Bindings

    override fun simpleInitApp() {
        initScriptEngine()
        loadKotlin()


        startScreenState = StartScreenState(this)
        stateManager.attach(startScreenState)

        bulletAppState = BulletAppState()
        stateManager.attach(bulletAppState)
    }

    override fun simpleUpdate(tpf: Float) {
        if (scriptCode.isPresent) {
            try {
                println("Processing Kotlin: " + scriptCode.get())
                val app = this
                val startTime = Instant.now()
                val result = scriptEngine.eval(scriptCode.get(), scriptGlobal)
                val scriptScreenState = getStateManager().getState(ScriptScreenState::class.java)
                scriptScreenState?.writeToConsole("> " + result)
                println(result)
                println(Duration.between(startTime, Instant.now()))
            } catch (e: Exception) {
                val scriptScreenState = getStateManager().getState(ScriptScreenState::class.java)
                scriptScreenState?.writeToConsole("Kotlin runtime error: " + e.message)
                println(e.message)
                loadKotlin()
            }

            scriptCode = Optional.empty<String>()
        }
    }

    override fun simpleRender(rm: RenderManager?) {
        //TODO: add render code
    }

    fun initScriptEngine() {
        scriptEngine = ScriptEngineManager().getEngineByExtension("kts")!! as KotlinJsr223JvmLocalScriptEngine
    }

    fun loadKotlin() {
        try {
//            val code = String(Files.readAllBytes(Paths.get("assets/Scripts/Kotlin/core.kt")))
//            val compiled = scriptEngine.compile(code)
            scriptGlobal = scriptEngine.createBindings()
            scriptGlobal.put("app", this)
//            scriptGlobal.put("z", 5)
//            scriptGlobal.put("core", compiled.eval(scriptGlobal))
        } catch (e: IOException) {
            println("File does not exist: " + e.message)
        } catch (e: ScriptException) {
            println("Kotlin compile error: " + e.message)
        }

    }

    companion object {


        @JvmStatic fun main(args: Array<String>) {
            val app = Main()
            app.isShowSettings = false
            val settings = AppSettings(true)
            settings.put("Width", 1280)
            settings.put("Height", 720)
            settings.put("Title", "Pong")
            settings.isVSync = true
            //        settings.setFullscreen(true);
            settings.frameRate = 60 // set to less than or equal screen refresh rate
            //        settings.setSamples(2);
            app.setSettings(settings)
            app.start()
        }
    }
}
