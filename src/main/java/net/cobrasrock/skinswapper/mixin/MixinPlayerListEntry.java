package net.cobrasrock.skinswapper.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.cobrasrock.skinswapper.SkinChangeManager;
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
            if(SkinChangeManager.isSettingsChanged()){
                SkinChangeManager.initialized = false;
            }

            //sets skin when switching world
            if(textures.get(MinecraftProfileTexture.Type.SKIN) == null) {
                SkinChangeManager.initializeSkin();
            }

            //sets skin when launching game
            if(!SkinChangeManager.initialized) {
                SkinChangeManager.initializeSkin();
            }

            //loads skin
            if(SkinChangeManager.skinChanged) {
                textures.put(MinecraftProfileTexture.Type.SKIN, SkinChangeManager.skinId);
                model = SkinChangeManager.skinType;
                SkinChangeManager.skinChanged = false;
            }

            ci.cancel();
        }
    }
}