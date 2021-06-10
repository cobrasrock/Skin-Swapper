package net.skinchange.mixin;

import java.util.HashMap;
import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.texture.PlayerSkinProvider;

@Mixin(PlayerSkinProvider.class)
public abstract class MixinPlayerSkinProvider{

	@Accessor("sessionService")
	public abstract MinecraftSessionService getSessionService();

	@Inject(at = @At("HEAD"), method = "loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V")
	private void loadSkin(GameProfile profile, PlayerSkinProvider.SkinTextureAvailableCallback callback, boolean requireSecure, CallbackInfo ci)
	{
		HashMap map = Maps.newHashMap();
		try {
			map.putAll(getSessionService().getTextures(profile, requireSecure));
		} catch (InsecureTextureException var7) {
		}

		if (map.isEmpty()) {
			getSessionService().fillProfileProperties(profile, requireSecure);
		}
	}
}