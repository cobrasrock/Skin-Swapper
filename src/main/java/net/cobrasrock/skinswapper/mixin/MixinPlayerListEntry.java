package net.cobrasrock.skinswapper.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.cobrasrock.skinswapper.SkinChangeHandler;
import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public class MixinPlayerListEntry {

    @Shadow @Final private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Shadow private @Nullable String model;

    @Shadow @Final private GameProfile profile;

    @Inject(at = @At("HEAD"), method = "loadTextures", cancellable = true)
    private void loadTextures(CallbackInfo ci){

        //sets skin when launching game
        if(!SkinChangeHandler.isInitialized() && SkinSwapperConfig.offlineMode) {
            SkinChangeHandler.initializeSkin();
        }

        //sets offline skin
        if(SkinChangeHandler.isSkinChanged() && profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
            textures.put(MinecraftProfileTexture.Type.SKIN, SkinChangeHandler.getSkinId());
            model = SkinChangeHandler.getSkinType();
            ci.cancel();
        }
    }
}