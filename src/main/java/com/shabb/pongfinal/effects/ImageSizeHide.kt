package com.shabb.pongfinal.effects

import de.lessvoid.nifty.Nifty
import de.lessvoid.nifty.effects.EffectImpl
import de.lessvoid.nifty.effects.EffectProperties
import de.lessvoid.nifty.effects.Falloff
import de.lessvoid.nifty.elements.Element
import de.lessvoid.nifty.render.NiftyRenderEngine
import de.lessvoid.nifty.tools.LinearInterpolator
import de.lessvoid.nifty.tools.SizeValue

class ImageSizeHide : EffectImpl {
    private var startSize: Float = 0.toFloat()
    private var endSize: Float = 0.toFloat()
    private var imageSize = SizeValue("100%")
    private val interpolator: LinearInterpolator? = null

    override fun activate(
            nifty: Nifty,
            element: Element,
            parameter: EffectProperties) {
        //-- Scale
        startSize = java.lang.Float.parseFloat(parameter.getProperty("startSize", "1.0"))
        endSize = java.lang.Float.parseFloat(parameter.getProperty("endSize", "2.0"))

        // for hover mode only
        val maxSizeString = parameter.getProperty("maxSize")
        if (maxSizeString != null) {
            imageSize = SizeValue(maxSizeString)
        }

        nifty.renderEngine.setImageScale(startSize)

    }

    override fun execute(
            element: Element,
            normalizedTime: Float,
            falloff: Falloff?,
            r: NiftyRenderEngine) {
        //-- Hide
        r.setColorAlpha(1.0f + normalizedTime * -1.0f)

        //-- Scale
        r.setImageScale(startSize + normalizedTime * (endSize - startSize))
    }

    override fun deactivate() {}
}
