package net.skinchange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.skinchange.gui.SkinScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    //prevents player from being invisible
    @Inject(at = @At("HEAD"), method = "hasLabel", cancellable = true)
    public void hasLabel(CallbackInfoReturnable<Boolean> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            cir.setReturnValue(false);
        }
    }
}
