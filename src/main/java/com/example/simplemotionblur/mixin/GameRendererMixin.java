package com.example.simplemotionblur.mixin;

import com.example.simplemotionblur.MotionBlurRenderer;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Runs at the very end of renderWorld(): the 3D world (and your hand) is finished,
    // the HUD and menus have NOT been drawn yet, so they stay perfectly sharp.
    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void simplemotionblur$applyBlur(CallbackInfo ci) {
        MotionBlurRenderer.apply();
    }
}

