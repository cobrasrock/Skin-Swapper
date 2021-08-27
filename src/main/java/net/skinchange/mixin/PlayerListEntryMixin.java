package net.skinchange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.skinchange.gui.SkinScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {

    @Inject(at = @At("HEAD"), method = "loadTextures", cancellable = true)
    public void loadTextures(CallbackInfo ci){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getModel", cancellable = true)
    public void getModel(CallbackInfoReturnable<String> cir) {
        if (MinecraftClient.getInstance().currentScreen instanceof SkinScreen) {
            cir.setReturnValue(SkinScreen.getSelected().skinType);
        }
    }
}
