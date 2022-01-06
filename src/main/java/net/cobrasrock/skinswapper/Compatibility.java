package net.cobrasrock.skinswapper;

import dev.tr7zw.skinlayers.SkinLayersModBase;
import net.cobrasrock.skinswapper.mixin.compatibility.skinlayers.ConfigAccessor;
import net.earthcomputer.multiconnect.connect.ServersExt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;

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

    public static void onOfflineSkinChange(){
        try {
            SkinLayersModBase.instance.refreshLayers(MinecraftClient.getInstance().player);
        } catch (NoClassDefFoundError ignored){
            //3d skin layers not installed
        }
    }

    //slightly slower but works
    public static boolean onOnlineSkinChange(String hotname, int port){
        try {
            ServersExt.getInstance(); //checks if multiconnect is installed
            ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), MinecraftClient.getInstance(), new ServerAddress(hotname, port), null);
            return true;
        } catch (NoClassDefFoundError ignored){
            //multiconnect not installed
            return false;
        }
    }
}
