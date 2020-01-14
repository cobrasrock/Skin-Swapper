package net.skinchange.gui;

import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.mojang.blaze3d.platform.GlStateManager;

import java.io.File;


public class SkinEntry extends AlwaysSelectedEntryListWidget.Entry<SkinEntry>
{    
    protected final MinecraftClient client;
    public String fname;
    public File skin_icon;
    public boolean oldSkin;
    public String skinType;
    private NativeImage nativeImage;
    protected Identifier iconLocation;
    protected final SkinListWidget list;
    private Identifier id;
    private NativeImageBackedTexture nImage;
    private int maxNameWidth;

    public SkinEntry(String text, File f, SkinListWidget list, NativeImage img, boolean old)
    {
        fname = text;
        this.client = MinecraftClient.getInstance();
        skin_icon = f;
        this.list = list;
        nativeImage = img;
        oldSkin = old;
        id = new Identifier("skins");
        nImage = new NativeImageBackedTexture(nativeImage);
        this.skinType = genSkinType();
    }

    @Override
    public void render(int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta)
    {
        TextRenderer font = this.client.textRenderer;
        renderimg(x,y);
        maxNameWidth = rowWidth - 32 - 3;
        if (font.getStringWidth(fname) > maxNameWidth)
        {
			fname = font.trimToWidth(fname, maxNameWidth - font.getStringWidth("...")) + "...";
		}
        font.draw(fname, x + 35, y + 5, 0xFFFFFF);

    }

    @Override
    public boolean mouseClicked(double v, double v1, int i)
    {
        list.select(this);
		return true;
    }
    
    private void renderimg(int k, int j)
    {
        this.client.getTextureManager().registerTexture(id, nImage);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(id);
        GlStateManager.enableBlend();
        if(oldSkin == true)
        {
            //head icon
            DrawableHelper.blit(k, j, 32.0F, 32.0F, 32, 32, 256, 128);
            if(!skin_icon.getName().equals("Notch.png")) //Notch's skin uses black instead of transparency
            {
                DrawableHelper.blit(k, j, 160.0F, 32.0F, 32, 32, 256, 128);
            }
        }
        else
        {
            //head icon
            DrawableHelper.blit(k, j, 32.0F, 32.0F, 32, 32, 256, 256);
            DrawableHelper.blit(k, j, 160.0F, 32.0F, 32, 32, 256, 256);
        }
        GlStateManager.disableBlend();
    }

    public void toggleSkinType()
    {
        if(skinType == "Classic")
        {
            skinType = "Slim";
        }
        else
        {
            skinType = "Classic";
        }
    }

    public String genSkinType()
    {
        try{
            BufferedImage image = ImageIO.read(skin_icon);
            int pixel = image.getRGB(50,19);

            if((pixel>>24) == 0x00)
            {
                return "Slim";
            }
            return "Classic";
        }catch(Exception e){}
        return null;
    }

    public void deleteSkin()
    {
        skin_icon.delete();
    }

    public void drawSkin(int k, int j)
    {
        this.client.getTextureManager().registerTexture(id, nImage);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(id);
        GlStateManager.enableBlend();
        if(oldSkin == true)
        {
            //head icon
            DrawableHelper.blit(k, j, 32.0F, 32.0F, 32, 32, 256, 128);
            if(!skin_icon.getName().equals("Notch.png")) //Notch's skin uses black instead of transparency
            {
                DrawableHelper.blit(k, j, 160.0F, 32.0F, 32, 32, 256, 128);
            }

            //body
            DrawableHelper.blit(k, j+32, 80.0F, 80.0F, 32, 48, 256, 128);

            //arm

            //left
            if(skinType.equals("Slim"))
            {
                DrawableHelper.blit(k-12, j+32, 176.0F, 80.0F, 12, 48, 256, 128);
            }

            else
            {
                DrawableHelper.blit(k-16, j+32, 176.0F, 80.0F, 16, 48, 256, 128);
            }

            //right(flips horizontal)
            if(skinType.equals("Slim"))
            {
                DrawableHelper.blit(k+32, j+32, 184F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.blit(k+36, j+32, 180F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.blit(k+40, j+32, 176F, 80.0F, 4, 48, 256, 128);
            }

            else
            {
                DrawableHelper.blit(k+32, j+32, 188F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.blit(k+36, j+32, 184F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.blit(k+40, j+32, 180F, 80.0F, 4, 48, 256, 128);
                DrawableHelper.blit(k+44, j+32, 176F, 80.0F, 4, 48, 256, 128);
            }


            //leg

            //left
            DrawableHelper.blit(k, j+80, 16.0F, 80.0F, 16, 48, 256, 128);

            //right(flips horizontal)
            DrawableHelper.blit(k+28, j+80, 16.0F, 80.0F, 4, 48, 256, 128);
            DrawableHelper.blit(k+24, j+80, 20.0F, 80.0F, 4, 48, 256, 128);
            DrawableHelper.blit(k+20, j+80, 24.0F, 80.0F, 4, 48, 256, 128);
            DrawableHelper.blit(k+16, j+80, 28.0F, 80.0F, 4, 48, 256, 128);
        }
        else
        {
            //head icon
            DrawableHelper.blit(k, j, 32.0F, 32.0F, 32, 32, 256, 256);
            DrawableHelper.blit(k, j, 160.0F, 32.0F, 32, 32, 256, 256);

            //body
            DrawableHelper.blit(k, j+32, 80.0F, 80.0F, 32, 48, 256, 256);
            DrawableHelper.blit(k, j+32, 80.0F, 144.0F, 32, 48, 256, 256);

            //left arm
            if(skinType.equals("Slim"))
            {
                DrawableHelper.blit(k-12, j+32, 176.0F, 80.0F, 12, 48, 256, 256);
                DrawableHelper.blit(k-12, j+32, 176.0F, 144.0F, 12, 48, 256, 256);
            }

            else
            {
                DrawableHelper.blit(k-16, j+32, 176.0F, 80.0F, 16, 48, 256, 256);
                DrawableHelper.blit(k-16, j+32, 176.0F, 144.0F, 16, 48, 256, 256);
            }

            //right arm
            if(skinType.equals("Slim"))
            {
                DrawableHelper.blit(k+32, j+32, 144.0F, 208.0F, 12, 48, 256, 256);
                DrawableHelper.blit(k+32, j+32, 208.0F, 208.0F, 12, 48, 256, 256);
            }
            else
            {
                DrawableHelper.blit(k+32, j+32, 144.0F, 208.0F, 16, 48, 256, 256);
                DrawableHelper.blit(k+32, j+32, 208.0F, 208.0F, 16, 48, 256, 256);
            }

            //reght leg
            DrawableHelper.blit(k+16, j+80, 16.0F, 80.0F, 16, 48, 256, 256);
            DrawableHelper.blit(k+16, j+80, 16.0F, 144.0F, 16, 48, 256, 256);

            //left leg
            DrawableHelper.blit(k, j+80, 80.0F, 208.0F, 16, 48, 256, 256);
            DrawableHelper.blit(k, j+80, 16.0F, 208.0F, 16, 48, 256, 256);
        }
        GlStateManager.disableBlend();
    }
}