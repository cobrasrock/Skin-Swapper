package net.skinchange.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.Identifier;
import net.skinchange.gui.SkinScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.util.DefaultSkinHelper;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "isSpectator", cancellable = true)
    public void isSpectator(CallbackInfoReturnable<Boolean> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            try {
                cir.setReturnValue(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getPlayerListEntry", cancellable = true)
    public void getPlayerListEntry(CallbackInfoReturnable<PlayerListEntry> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            try {
                cir.setReturnValue(new PlayerListEntry(new PlayerListS2CPacket.Entry(new GameProfile(UUIDTypeAdapter.fromString("fd420d0a4aa140e195b7558fb7577e50"), "cobrasrock"), 0, null, null)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getSkinTexture", cancellable = true)
    public void getSkinTexture(CallbackInfoReturnable<Identifier> cir){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen) {
            cir.setReturnValue(SkinScreen.getSelected().processedSkin);
        }
    }


}
