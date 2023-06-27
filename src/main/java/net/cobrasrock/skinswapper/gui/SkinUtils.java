package net.cobrasrock.skinswapper.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.cobrasrock.skinswapper.Compatibility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

import net.cobrasrock.skinswapper.config.SkinSwapperConfig;

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


    /**
     * Creates a dummy player.
     * @return A dummy player
     */
    public static LivingEntity getDummyPlayer() throws Exception{
        if(MinecraftClient.getInstance().player != null && SkinSwapperConfig.showArmor) return MinecraftClient.getInstance().player;

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

    //draws 3d player in menu
    public static void drawPlayer(DrawContext context, int x, int y, int height, int mouseX, int mouseY) throws Exception{
        Compatibility.startSkinPreview();
        InventoryScreen.drawEntity(context, x, y, height, x-mouseX, (y-160)-mouseY, getDummyPlayer());
        Compatibility.stopSkinPreview();
    }

    //taken from yarn mappings, fixes the skin file
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
                int k = image.getColor(l, m);
                if ((k >> 24 & 255) < 128) {
                    return;
                }
            }
        }

        for(l = x1; l < x2; ++l) {
            for(m = y1; m < y2; ++m) {
                image.setColor(l, m, image.getColor(l, m) & 16777215);
            }
        }

    }

    private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2) {
        for(int i = x1; i < x2; ++i) {
            for(int j = y1; j < y2; ++j) {
                image.setColor(i, j, image.getColor(i, j) | -16777216);
            }
        }
    }

    //draws a 2d render of the skin, legacy method
    public static void drawSkin(DrawContext context, int k, int j, SkinEntry entry) {

        boolean oldSkin = entry.oldSkin;
        Identifier rawSkin = entry.rawSkin;
        File skinFile = entry.skin_file;
        SkinType skinType = entry.skinType;

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, rawSkin);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if(oldSkin)
        {
            //head icon
            context.drawTexture(rawSkin, k, j, 32.0F, 32.0F, 32, 32, 256, 128);
            if(!skinFile.getName().equals("Notch.png")) //Notch's skin uses black instead of transparency
            {
                context.drawTexture(rawSkin, k, j, 160.0F, 32.0F, 32, 32, 256, 128);
            }

            //body
            context.drawTexture(rawSkin, k, j+32, 80.0F, 80.0F, 32, 48, 256, 128);

            //arm

            //left
            if(skinType.equals(SkinType.SLIM))
            {
                context.drawTexture(rawSkin, k-12, j+32, 176.0F, 80.0F, 12, 48, 256, 128);
            }

            else
            {
                context.drawTexture(rawSkin, k-16, j+32, 176.0F, 80.0F, 16, 48, 256, 128);
            }

            //right(flips horizontal)
            if(skinType.equals(SkinType.SLIM))
            {
                context.drawTexture(rawSkin, k+32, j+32, 184F, 80.0F, 4, 48, 256, 128);
                context.drawTexture(rawSkin, k+36, j+32, 180F, 80.0F, 4, 48, 256, 128);
                context.drawTexture(rawSkin, k+40, j+32, 176F, 80.0F, 4, 48, 256, 128);
            }

            else
            {
                context.drawTexture(rawSkin, k+32, j+32, 188F, 80.0F, 4, 48, 256, 128);
                context.drawTexture(rawSkin, k+36, j+32, 184F, 80.0F, 4, 48, 256, 128);
                context.drawTexture(rawSkin, k+40, j+32, 180F, 80.0F, 4, 48, 256, 128);
                context.drawTexture(rawSkin, k+44, j+32, 176F, 80.0F, 4, 48, 256, 128);
            }


            //leg

            //left
            context.drawTexture(rawSkin, k, j+80, 16.0F, 80.0F, 16, 48, 256, 128);

            //right(flips horizontal)
            context.drawTexture(rawSkin, k+28, j+80, 16.0F, 80.0F, 4, 48, 256, 128);
            context.drawTexture(rawSkin, k+24, j+80, 20.0F, 80.0F, 4, 48, 256, 128);
            context.drawTexture(rawSkin, k+20, j+80, 24.0F, 80.0F, 4, 48, 256, 128);
            context.drawTexture(rawSkin, k+16, j+80, 28.0F, 80.0F, 4, 48, 256, 128);
        }

        else
        {
            //head icon
            context.drawTexture(rawSkin, k, j, 32.0F, 32.0F, 32, 32, 256, 256);
            context.drawTexture(rawSkin, k, j, 160.0F, 32.0F, 32, 32, 256, 256);

            //body
            context.drawTexture(rawSkin, k, j+32, 80.0F, 80.0F, 32, 48, 256, 256);
            context.drawTexture(rawSkin, k, j+32, 80.0F, 144.0F, 32, 48, 256, 256);

            //left arm
            if(skinType.equals(SkinType.SLIM))
            {
                context.drawTexture(rawSkin, k-12, j+32, 176.0F, 80.0F, 12, 48, 256, 256);
                context.drawTexture(rawSkin, k-12, j+32, 176.0F, 144.0F, 12, 48, 256, 256);
            }

            else
            {
                context.drawTexture(rawSkin, k-16, j+32, 176.0F, 80.0F, 16, 48, 256, 256);
                context.drawTexture(rawSkin, k-16, j+32, 176.0F, 144.0F, 16, 48, 256, 256);
            }

            //right arm
            if(skinType.equals(SkinType.SLIM))
            {
                context.drawTexture(rawSkin, k+32, j+32, 144.0F, 208.0F, 12, 48, 256, 256);
                context.drawTexture(rawSkin, k+32, j+32, 208.0F, 208.0F, 12, 48, 256, 256);
            }
            else
            {
                context.drawTexture(rawSkin, k+32, j+32, 144.0F, 208.0F, 16, 48, 256, 256);
                context.drawTexture(rawSkin, k+32, j+32, 208.0F, 208.0F, 16, 48, 256, 256);
            }

            //reght leg
            context.drawTexture(rawSkin, k+16, j+80, 16.0F, 80.0F, 16, 48, 256, 256);
            context.drawTexture(rawSkin, k+16, j+80, 16.0F, 144.0F, 16, 48, 256, 256);

            //left leg
            context.drawTexture(rawSkin, k, j+80, 80.0F, 208.0F, 16, 48, 256, 256);
            context.drawTexture(rawSkin, k, j+80, 16.0F, 208.0F, 16, 48, 256, 256);
        }
    }
}
