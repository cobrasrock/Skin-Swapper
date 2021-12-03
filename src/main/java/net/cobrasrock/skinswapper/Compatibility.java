package net.cobrasrock.skinswapper;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import net.cobrasrock.skinswapper.mixin.compatibility.skinlayers.ConfigAccessor;

public class Compatibility {

    public static void startSkinPreview(){
        try {
            ((ConfigAccessor)(SkinLayersModBase.config)).setEnableSkulls(false);
        } catch (NoClassDefFoundError ignored){
            //3d skin layers not installed
        }
    }

    public static void stopSkinPreview(){
        try {
            ((ConfigAccessor)(SkinLayersModBase.config)).setEnableSkulls(true);
        } catch (NoClassDefFoundError ignored){
            //3d skin layers not installed
        }
    }
}