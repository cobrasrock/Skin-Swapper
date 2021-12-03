package net.cobrasrock.skinswapper.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerSkinProvider.class)
public class MixinPlayerSkinProvider{

	@Shadow @Final private MinecraftSessionService sessionService;

	//refreshes skin for singleplayer
	@Inject(at = @At("HEAD"), method = "loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V")
	private void loadSkin(GameProfile profile, PlayerSkinProvider.SkinTextureAvailableCallback callback, boolean requireSecure, CallbackInfo ci) {
		if (sessionService.getTextures(profile, requireSecure).isEmpty()) {
			sessionService.fillProfileProperties(profile, requireSecure);
		}
	}
}