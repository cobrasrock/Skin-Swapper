package net.cobrasrock.skinswapper;

import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.cobrasrock.skinswapper.gui.SkinType;
import net.cobrasrock.skinswapper.gui.SkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SkinChangeHandler {
    private static boolean skinChanged;
    private static String skinType;
    private static Identifier skinId;
    private static boolean initialized = false;

    public static void onSkinChange(SkinType type, File skinFile){
        skinChanged = true;

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

        //if in offline mode, write to file
        if(SkinSwapperConfig.offlineMode){

            //creates new file
            try {
                File dest = new File("config" + File.separator + "skinswapper_lastskin_" + skinType + ".png");
                Files.copy(skinFile.toPath(), dest.toPath());
            } catch (IOException ignored){}

            File oldFile;

            //deletes old skin file
            if(type == SkinType.CLASSIC) {
                oldFile = new File("config" + File.separator + "skinswapper_lastskin_slim.png");
            }
            else {
                oldFile = new File("config" + File.separator + "skinswapper_lastskin_default.png");
            }

            oldFile.delete();
        }
    }

    //sets skin to match files on launch
    public static void initializeSkin(){
        File skinFile = new File("config" + File.separator + "skinswapper_lastskin_default.png");
        SkinType skinType = SkinType.CLASSIC;

        //checks if skin is classic or slim
        if(!skinFile.exists()){
            skinFile = new File("config" + File.separator + "skinswapper_lastskin_slim.png");
            skinType = SkinType.SLIM;
        }

        //no skin file available
        if(!skinFile.exists()){
            return;
        }

        //schedules skin change
        SkinChangeHandler.onSkinChange(skinType, skinFile);
        initialized = true;
    }

    public static boolean isInitialized(){
        return initialized;
    }

    public static boolean isSkinChanged(){
        return skinChanged;
    }

    public static String getSkinType(){
        return skinType;
    }

    public static Identifier getSkinId(){
        return skinId;
    }
}
