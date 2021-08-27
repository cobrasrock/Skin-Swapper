package net.skinchange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.skinchange.gui.SkinScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Inject(at = @At("HEAD"), method = "getSquaredDistanceToCamera(Lnet/minecraft/entity/Entity;)D", cancellable = true)
    public void getSquaredDistanceToCamera(Entity entity, CallbackInfoReturnable<Double> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            cir.setReturnValue(0D);
        }
    }

    @Inject(at = @At("HEAD"), method = "getSquaredDistanceToCamera(DDD)D", cancellable = true)
    public void getSquaredDistanceToCamera(double x, double y, double z, CallbackInfoReturnable<Double> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            cir.setReturnValue(0D);
        }
    }
}
