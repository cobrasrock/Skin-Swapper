package net.cobrasrock.skinswapper.mixin.preview;

import net.cobrasrock.skinswapper.gui.SkinScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "isSpectator", cancellable = true)
    public void isSpectator(CallbackInfoReturnable<Boolean> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            cir.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "getModel", cancellable = true)
    public void getModel(CallbackInfoReturnable<String> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            cir.setReturnValue(SkinScreen.getSelected().skinType.getName());
        }
    }

    @Inject(at = @At("HEAD"), method = "getPlayerListEntry", cancellable = true)
    public void getPlayerListEntry(CallbackInfoReturnable<PlayerListEntry> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            cir.setReturnValue(null);
        }
    }

    @Inject(at = @At("HEAD"), method = "getSkinTexture", cancellable = true)
    public void getSkinTexture(CallbackInfoReturnable<Identifier> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen) {
            cir.setReturnValue(SkinScreen.getSelected().processedSkin);
        }
    }
}
