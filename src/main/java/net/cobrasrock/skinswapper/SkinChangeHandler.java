package net.cobrasrock.skinswapper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.cobrasrock.skinswapper.gui.SkinType;
import net.cobrasrock.skinswapper.gui.SkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class SkinChangeHandler {
    public static boolean skinChanged;
    public static String skinType;
    public static Identifier skinId;
    public static boolean initialized = false;
    private static boolean wasOfflineMode = SkinSwapperConfig.offlineMode;

    public static void onSkinChange(SkinType type, File skinFile){

        if(type.equals(SkinType.SLIM)){
            skinType = "slim";
        }

        else {
            skinType = "default";
        }

        //registers texture
        skinId = new Identifier("skinswapper_skin");
        NativeImage rawNativeImage = SkinUtils.toNativeImage(skinFile);
        NativeImage processedNativeImage = SkinUtils.remapTexture(rawNativeImage);
        NativeImageBackedTexture processedImageBackedTexture = new NativeImageBackedTexture(processedNativeImage);
        MinecraftClient.getInstance().getTextureManager().registerTexture(skinId, processedImageBackedTexture);

        skinChanged = true;

        //if in offline mode, write to file
        if(SkinSwapperConfig.offlineMode){

            //deletes old skin file
            File oldFile;

            if(type == SkinType.CLASSIC) {
                oldFile = new File("config" + File.separator + "skinswapper_lastskin_slim.png");
            }
            else {
                oldFile = new File("config" + File.separator + "skinswapper_lastskin_default.png");
            }

            oldFile.delete();

            //creates new file
            try {
                File dest = new File("config" + File.separator + "skinswapper_lastskin_" + skinType + ".png");
                Files.copy(skinFile.toPath(), dest.toPath(), REPLACE_EXISTING);
            } catch (IOException ignored){}
        }
    }

    //sets skin to match files on launch
    public static void initializeSkin(){
        if(SkinSwapperConfig.offlineMode) {
            File skinFile = new File("config" + File.separator + "skinswapper_lastskin_default.png");
            SkinType skinType = SkinType.CLASSIC;

            //checks if skin is classic or slim
            if (!skinFile.exists()) {
                skinFile = new File("config" + File.separator + "skinswapper_lastskin_slim.png");
                skinType = SkinType.SLIM;
            }

            //no skin file available
            if (!skinFile.exists()) {
                return;
            }

            //schedules skin change
            SkinChangeHandler.onSkinChange(skinType, skinFile);
            initialized = true;
        }

        else {
            //loads from mojang
            try {
                GameProfile profile = MinecraftClient.getInstance().getSession().getProfile();

                profile.getProperties().clear();
                profile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = MinecraftClient.getInstance().getSessionService().getTextures(profile, false);

                MinecraftProfileTexture profileTexture = map.get(MinecraftProfileTexture.Type.SKIN);
                skinType = profileTexture.getMetadata("model");
                skinId = MinecraftClient.getInstance().getSkinProvider().loadSkin(profileTexture, MinecraftProfileTexture.Type.SKIN);
            }

            //failed to get skin, loads default
            catch (Exception ignored){
                GameProfile profile = MinecraftClient.getInstance().getSession().getProfile();
                skinId = DefaultSkinHelper.getTexture(profile.getId());
                skinType = DefaultSkinHelper.getModel(profile.getId());
            }

            finally {
                skinChanged = true;
                initialized = true;
            }
        }
    }

    //determines if the settings have changed
    public static boolean isSettingsChanged(){
        if(wasOfflineMode != SkinSwapperConfig.offlineMode){
            wasOfflineMode = SkinSwapperConfig.offlineMode;
            return true;
        }

        else {
            return false;
        }
    }
}
