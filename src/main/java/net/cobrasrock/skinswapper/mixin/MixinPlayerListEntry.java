package net.cobrasrock.skinswapper.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.cobrasrock.skinswapper.SkinChangeManager;
import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
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
public abstract class MixinPlayerListEntry {

    @Shadow @Final private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Shadow private @Nullable String model;

    @Shadow @Final private GameProfile profile;

    @Shadow private boolean texturesLoaded;

    @Shadow public abstract Identifier getSkinTexture();

    @Shadow public abstract String getModel();

    @Inject(at = @At("HEAD"), method = "loadTextures", cancellable = true)
    private void loadTextures(CallbackInfo ci){

        if(profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {

            if (SkinChangeManager.isSettingsChanged()) {
                if (SkinSwapperConfig.offlineMode) {
                    SkinChangeManager.onlineSkinId = MoreObjects.firstNonNull(this.textures.get(MinecraftProfileTexture.Type.SKIN), DefaultSkinHelper.getTexture(this.profile.getId()));
                    SkinChangeManager.onlineSkinType = getModel();

                    SkinChangeManager.initializeOfflineSkin();
                } else {
                    SkinChangeManager.skinId = SkinChangeManager.onlineSkinId;
                    SkinChangeManager.skinType = SkinChangeManager.onlineSkinType;
                    SkinChangeManager.skinChanged = true;
                }
            }

            if(SkinChangeManager.skinChanged){
                SkinChangeManager.skinChanged = false;
                textures.put(MinecraftProfileTexture.Type.SKIN, SkinChangeManager.skinId);
                model = SkinChangeManager.skinType;

                loadCapeTextures();
            }

            if(!(textures.get(MinecraftProfileTexture.Type.SKIN) == null)){
                ci.cancel();
            } else if (SkinChangeManager.skinId != null){
                textures.put(MinecraftProfileTexture.Type.SKIN, SkinChangeManager.skinId);
                model = SkinChangeManager.skinType;
                loadCapeTextures();
                ci.cancel();
            }
        }
    }

    //adapted from yarn mappings
    protected void loadCapeTextures() {
        PlayerListEntry playerListEntry = (PlayerListEntry)(Object) this;
        synchronized (playerListEntry) {
            if (!this.texturesLoaded) {
                this.texturesLoaded = true;
                MinecraftClient.getInstance().getSkinProvider().loadSkin(this.profile, (type, id, texture) -> {

                    if (type != MinecraftProfileTexture.Type.SKIN) {
                        this.textures.put(type, id);
                    }
                }, true);
            }
        }
    }
}