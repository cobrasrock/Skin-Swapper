package net.skinchange.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.world.ClientWorld;
import net.skinchange.changeskin.SkinChange;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

public class SkinUtils {

    public static OtherClientPlayerEntity player;

    public static NativeImage toNativeImage(File file){
        try {
            InputStream inputStream = new FileInputStream(file);
            NativeImage nativeImage = NativeImage.read(inputStream);
            inputStream.close();
            return nativeImage;
        }
        catch(Exception e) {
            //happens if there is no file found
            return null;
        }
    }

    public static OtherClientPlayerEntity getDummyPlayer() throws Exception{
        if(player == null){

            //hack to avoid creating a world
            Field f =Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);

            ClientWorld clientWorld = (ClientWorld) unsafe.allocateInstance(ClientWorld.class);
            GameProfile profile = (GameProfile) unsafe.allocateInstance(GameProfile.class);

            player = new OtherClientPlayerEntity(clientWorld, profile);
        }
        return player;
    }

    //draws player on in menu
    public static void drawPlayer(int x, int y, int height, int mouseX, int mouseY){
        try {
            InventoryScreen.drawEntity(x, y, height, x-mouseX, (y-160)-mouseY, getDummyPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //taken from source, fixes the skin file
    public static NativeImage remapTexture(NativeImage nativeImage) {
        int x = nativeImage.getWidth();
        int y = nativeImage.getHeight();
        if (x == 64 && (y == 32 || y == 64)) {
            boolean bl = y == 32;
            if (bl) {
                NativeImage nativeImage2 = new NativeImage(64, 64, true);
                nativeImage2.copyFrom(nativeImage);
                nativeImage.close();
                nativeImage = nativeImage2;
                nativeImage2.fillRect(0, 32, 64, 32, 0);
                nativeImage2.copyRect(4, 16, 16, 32, 4, 4, true, false);
                nativeImage2.copyRect(8, 16, 16, 32, 4, 4, true, false);
                nativeImage2.copyRect(0, 20, 24, 32, 4, 12, true, false);
                nativeImage2.copyRect(4, 20, 16, 32, 4, 12, true, false);
                nativeImage2.copyRect(8, 20, 8, 32, 4, 12, true, false);
                nativeImage2.copyRect(12, 20, 16, 32, 4, 12, true, false);
                nativeImage2.copyRect(44, 16, -8, 32, 4, 4, true, false);
                nativeImage2.copyRect(48, 16, -8, 32, 4, 4, true, false);
                nativeImage2.copyRect(40, 20, 0, 32, 4, 12, true, false);
                nativeImage2.copyRect(44, 20, -8, 32, 4, 12, true, false);
                nativeImage2.copyRect(48, 20, -16, 32, 4, 12, true, false);
                nativeImage2.copyRect(52, 20, -8, 32, 4, 12, true, false);
            }

            stripAlpha(nativeImage, 0, 0, 32, 16);
            if (bl) {
                stripColor(nativeImage, 32, 0, 64, 32);
            }

            stripAlpha(nativeImage, 0, 16, 64, 32);
            stripAlpha(nativeImage, 16, 48, 48, 64);
            return nativeImage;
        } else {
            nativeImage.close();
            return null;
        }
    }

    private static void stripColor(NativeImage image, int x1, int y1, int x2, int y2) {
        int l;
        int m;
        for(l = x1; l < x2; ++l) {
            for(m = y1; m < y2; ++m) {
                int k = image.getPixelColor(l, m);
                if ((k >> 24 & 255) < 128) {
                    return;
                }
            }
        }

        for(l = x1; l < x2; ++l) {
            for(m = y1; m < y2; ++m) {
                image.setPixelColor(l, m, image.getPixelColor(l, m) & 16777215);
            }
        }

    }

    private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2) {
        for(int i = x1; i < x2; ++i) {
            for(int j = y1; j < y2; ++j) {
                image.setPixelColor(i, j, image.getPixelColor(i, j) | -16777216);
            }
        }
    }
}
