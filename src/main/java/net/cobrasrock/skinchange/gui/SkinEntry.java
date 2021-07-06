package net.cobrasrock.skinchange.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;


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
    private static int identify = 0;

    public SkinEntry(String text, File f, SkinListWidget list, NativeImage img, boolean old)
    {
        identify++;
        fname = text;
        this.client = MinecraftClient.getInstance();
        skin_icon = f;
        this.list = list;
        nativeImage = img;
        oldSkin = old;
        id = new Identifier(""+identify);
        nImage = new NativeImageBackedTexture(nativeImage);
        this.skinType = genSkinType();
        this.client.getTextureManager().registerTexture(id, nImage);
    }

    public void render(MatrixStack matrices, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean isSelected, float delta)
    {
        TextRenderer font = this.client.textRenderer;
        renderimg(x,y,matrices);
        maxNameWidth = rowWidth - 32 - 3;
        if (font.getWidth(fname) > maxNameWidth)
        {
			fname = font.trimToWidth(fname, maxNameWidth - font.getWidth("...")) + "...";
		}
        font.draw(matrices, fname, x + 35, y + 5, 0xFFFFFF);

    }

    @Override
    public boolean mouseClicked(double v, double v1, int i)
    {
        list.select(this);
		return true;
    }
    
    private void renderimg(int k, int j, MatrixStack matrices)
    {
        
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(id);
        RenderSystem.enableBlend();
        if(oldSkin == true)
        {
            //head icon
            drawTexture(matrices, k, j, 32.0F, 32.0F, 32, 32, 256, 128);
            if(!skin_icon.getName().equals("Notch.png")) //Notch's skin uses black instead of transparency
            {
                drawTexture(matrices, k, j, 160.0F, 32.0F, 32, 32, 256, 128);
            }
        }
        else
        {
            //head icon
            drawTexture(matrices, k, j, 32.0F, 32.0F, 32, 32, 256, 256);
            drawTexture(matrices, k, j, 160.0F, 32.0F, 32, 32, 256, 256);
        }
        RenderSystem.disableBlend();
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

    public void drawSkin(int k, int j, MatrixStack matrices)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(id);
        GlStateManager.enableBlend();
        if(oldSkin == true)
        {
            //head icon
            drawTexture(matrices, k, j, 32.0F, 32.0F, 32, 32, 256, 128);
            if(!skin_icon.getName().equals("Notch.png")) //Notch's skin uses black instead of transparency
            {
                drawTexture(matrices, k, j, 160.0F, 32.0F, 32, 32, 256, 128);
            }

            //body
            drawTexture(matrices, k, j+32, 80.0F, 80.0F, 32, 48, 256, 128);

            //arm

            //left
            if(skinType.equals("Slim"))
            {
                drawTexture(matrices, k-12, j+32, 176.0F, 80.0F, 12, 48, 256, 128);
            }

            else
            {
                drawTexture(matrices, k-16, j+32, 176.0F, 80.0F, 16, 48, 256, 128);
            }

            //right(flips horizontal)
            if(skinType.equals("Slim"))
            {
                drawTexture(matrices, k+32, j+32, 184F, 80.0F, 4, 48, 256, 128);
                drawTexture(matrices, k+36, j+32, 180F, 80.0F, 4, 48, 256, 128);
                drawTexture(matrices, k+40, j+32, 176F, 80.0F, 4, 48, 256, 128);
            }

            else
            {
                drawTexture(matrices, k+32, j+32, 188F, 80.0F, 4, 48, 256, 128);
                drawTexture(matrices, k+36, j+32, 184F, 80.0F, 4, 48, 256, 128);
                drawTexture(matrices, k+40, j+32, 180F, 80.0F, 4, 48, 256, 128);
                drawTexture(matrices, k+44, j+32, 176F, 80.0F, 4, 48, 256, 128);
            }


            //leg

            //left
            drawTexture(matrices, k, j+80, 16.0F, 80.0F, 16, 48, 256, 128);

            //right(flips horizontal)
            drawTexture(matrices, k+28, j+80, 16.0F, 80.0F, 4, 48, 256, 128);
            drawTexture(matrices, k+24, j+80, 20.0F, 80.0F, 4, 48, 256, 128);
            drawTexture(matrices, k+20, j+80, 24.0F, 80.0F, 4, 48, 256, 128);
            drawTexture(matrices, k+16, j+80, 28.0F, 80.0F, 4, 48, 256, 128);
        }
        else
        {
            //head icon
            drawTexture(matrices, k, j, 32.0F, 32.0F, 32, 32, 256, 256);
            drawTexture(matrices, k, j, 160.0F, 32.0F, 32, 32, 256, 256);

            //body
            drawTexture(matrices, k, j+32, 80.0F, 80.0F, 32, 48, 256, 256);
            drawTexture(matrices, k, j+32, 80.0F, 144.0F, 32, 48, 256, 256);

            //left arm
            if(skinType.equals("Slim"))
            {
                drawTexture(matrices, k-12, j+32, 176.0F, 80.0F, 12, 48, 256, 256);
                drawTexture(matrices, k-12, j+32, 176.0F, 144.0F, 12, 48, 256, 256);
            }

            else
            {
                drawTexture(matrices, k-16, j+32, 176.0F, 80.0F, 16, 48, 256, 256);
                drawTexture(matrices, k-16, j+32, 176.0F, 144.0F, 16, 48, 256, 256);
            }

            //right arm
            if(skinType.equals("Slim"))
            {
                drawTexture(matrices, k+32, j+32, 144.0F, 208.0F, 12, 48, 256, 256);
                drawTexture(matrices, k+32, j+32, 208.0F, 208.0F, 12, 48, 256, 256);
            }
            else
            {
                drawTexture(matrices, k+32, j+32, 144.0F, 208.0F, 16, 48, 256, 256);
                drawTexture(matrices, k+32, j+32, 208.0F, 208.0F, 16, 48, 256, 256);
            }

            //reght leg
            drawTexture(matrices, k+16, j+80, 16.0F, 80.0F, 16, 48, 256, 256);
            drawTexture(matrices, k+16, j+80, 16.0F, 144.0F, 16, 48, 256, 256);

            //left leg
            drawTexture(matrices, k, j+80, 80.0F, 208.0F, 16, 48, 256, 256);
            drawTexture(matrices, k, j+80, 16.0F, 208.0F, 16, 48, 256, 256);
        }
        GlStateManager.disableBlend();
    }
}