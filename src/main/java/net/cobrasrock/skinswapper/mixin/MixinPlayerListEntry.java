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

        if(profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
            //sets skin when changing settings
            if(SkinChangeHandler.isSettingsChanged()){
                SkinChangeHandler.initialized = false;
            }

            //sets skin when switching world
            if(textures.get(MinecraftProfileTexture.Type.SKIN) == null) {
                SkinChangeHandler.initializeSkin();
            }

            //sets skin when launching game
            if(!SkinChangeHandler.initialized) {
                SkinChangeHandler.initializeSkin();
            }

            //loads skin
            if(SkinChangeHandler.skinChanged) {
                textures.put(MinecraftProfileTexture.Type.SKIN, SkinChangeHandler.skinId);
                model = SkinChangeHandler.skinType;
                SkinChangeHandler.skinChanged = false;
            }

            ci.cancel();
        }
    }
}