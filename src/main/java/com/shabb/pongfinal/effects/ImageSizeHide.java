package com.shabb.pongfinal.effects;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.tools.LinearInterpolator;
import de.lessvoid.nifty.tools.SizeValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImageSizeHide implements EffectImpl {
    private float startSize;
    private float endSize;
    @Nonnull
    private SizeValue imageSize = new SizeValue("100%");
    @Nullable
    private LinearInterpolator interpolator;

    @Override
    public void activate(
            @Nonnull final Nifty nifty,
            @Nonnull final Element element,
            @Nonnull final EffectProperties parameter) {
        //-- Scale
        startSize = Float.parseFloat(parameter.getProperty("startSize", "1.0"));
        endSize = Float.parseFloat(parameter.getProperty("endSize", "2.0"));

        // for hover mode only
        String maxSizeString = parameter.getProperty("maxSize");
        if (maxSizeString != null) {
            imageSize = new SizeValue(maxSizeString);
        }

        nifty.getRenderEngine().setImageScale(startSize);

    }

    @Override
    public void execute(
            @Nonnull final Element element,
            final float normalizedTime,
            @Nullable final Falloff falloff,
            @Nonnull final NiftyRenderEngine r) {
        //-- Hide
        float alpha;
        alpha = 1.0f + normalizedTime * -1.0f;

        if (alpha < 0.01) {
            r.setColorAlpha(0.0f);
//            element.setVisible(false);
        } else {
            r.setColorAlpha(alpha);
        }

        //-- Scale
        float scale;
        if (falloff == null) {
            if (interpolator != null) {
                scale = interpolator.getValue(normalizedTime);
            } else {
                scale = startSize + normalizedTime * (endSize - startSize);
            }
        } else {
            scale = 1.0f + falloff.getFalloffValue() * imageSize.getValue(1.0f);
        }
        r.setImageScale(scale);
    }

    @Override
    public void deactivate() {
    }
}
