package net.cobrasrock.skinswapper;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import net.cobrasrock.skinswapper.mixin.compatibility.skinlayers.ConfigAccessor;
import net.minecraft.client.MinecraftClient;


public class Compatibility {
    private static boolean modify = false;

    public static void startSkinPreview() {
        try {
            if (((ConfigAccessor) (SkinLayersModBase.config)).getEnableSkulls()) {
                modify = true;
                ((ConfigAccessor) (SkinLayersModBase.config)).setEnableSkulls(false);
            }
        } catch (NoClassDefFoundError ignored) {
            //3d skin layers not installed
        }
    }

    public static void stopSkinPreview() {
        try {
            if (modify) ((ConfigAccessor) (SkinLayersModBase.config)).setEnableSkulls(true);
        } catch (NoClassDefFoundError ignored) {
            //3d skin layers not installed
        }
    }

    public static void onOfflineSkinChange() {
        try {
            SkinLayersModBase.instance.refreshLayers(MinecraftClient.getInstance().player);
        } catch (NoClassDefFoundError ignored) {
            //3d skin layers not installed
        }
    }
}
