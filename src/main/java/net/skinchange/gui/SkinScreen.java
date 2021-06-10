package net.skinchange.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.skinchange.config.SkinSwapperConfig;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SkinScreen extends Screen
{
    final File folder;
    private Screen parent;
    private SkinListWidget skinList;
    private SkinListWidget previewList;
    private NativeImage nativeImage;
    private boolean oldSkin;
    private File file;
    public String error;
    

    public SkinScreen(Screen scr)
    {
        super(new LiteralText(""));
        this.parent = scr; 
        this.folder = new File("skins");
        this.file = new File("config" + File.separator + "skinchange" + File.separator + "data.txt");
    }
    
    @Override
    protected void init()
    {
        //does nothing but darken screen
        this.previewList = new SkinListWidget(this.client, this.width/2 + 4, this.height, 36 ,this.height-36, 36);
        this.previewList.setLeftPos(this.width/2+4);
        this.addSelectableChild(this.previewList);

        //lists skins
        this.skinList = new SkinListWidget(this.client, this.width/2 - 4, this.height, 36 ,this.height-52, 36);
        this.skinList.setLeftPos(0);
        this.addSelectableChild(this.skinList);

        addSkins(folder); //adds skins

        this.addDrawableChild(new ButtonWidget(this.width - this.previewList.getRowWidth()/2, this.height - 28, 100, 20, new TranslatableText("gui.back"), button -> MinecraftClient.getInstance().openScreen(parent))); //back

        this.addDrawableChild(new ButtonWidget(this.width - this.previewList.getRowWidth()/2 - 52 - 52, this.height - 28, 100, 20, new TranslatableText("skin.change_skin"), button -> //change skin
        {
            this.client.openScreen(new ConfirmScreen(this::changeSkin, new LiteralText(I18n.translate("skin.are_you_sure")), new LiteralText(I18n.translate("skin.changeto") + " '" + skinList.getSelected().fname + "'"), new TranslatableText("gui.yes"), new TranslatableText("gui.cancel")));
        })
        {
            @Override
            public void render(MatrixStack matrices, int var1, int var2, float var3)
            {
                visible = true;
                active = (skinList.getSelected() != null)&&(file.length() != 0);
                super.render(matrices, var1, var2, var3);
            }
        });

        this.addDrawableChild(new ButtonWidget(this.skinList.getRowWidth()/2 - 52 - 40 -4, this.height - 24, 100, 20, new TranslatableText("skin.open_folder"), button -> Util.getOperatingSystem().open(new File("skins")))); //open skin folder

        this.addDrawableChild(new ButtonWidget(this.skinList.getRowWidth()/2 + 52 - 40 -4, this.height - 24, 100, 20, new TranslatableText("skin.delete_skin"), button -> //delete skin
        {
            this.client.openScreen(new ConfirmScreen(this::removeEntry, new LiteralText(I18n.translate("skin.are_you_sure_remove")), new LiteralText("'"+skinList.getSelected().fname+"' "+I18n.translate("skin.long")) , new TranslatableText("selectWorld.delete"), new TranslatableText("gui.cancel")));
        })
        {
            @Override
            public void render(MatrixStack matrices, int var1, int var2, float var3)
            {
                visible = true;
                active = (skinList.getSelected() != null);
                super.render(matrices, var1, var2, var3);
            }
        });

        this.addDrawableChild(new ButtonWidget(this.skinList.getRowWidth()/2 - 52 - 40-4, this.height - 48, 100, 20, new TranslatableText("skin.classic"), button -> //classic select button
        {skinList.getSelected().toggleSkinType();})
        {
            @Override
            public void render(MatrixStack matrices, int var1, int var2, float var3)
            {
                visible = true;
                active = (skinList.getSelected() != null&& skinList.getSelected().skinType == "Slim");
                super.render(matrices, var1, var2, var3);
            }
        });

        this.addDrawableChild(new ButtonWidget(this.skinList.getRowWidth()/2 + 52 - 40-4, this.height - 48, 100, 20, new TranslatableText("skin.slim"), button -> //slim select button
        {skinList.getSelected().toggleSkinType();})
        {
            @Override
            public void render(MatrixStack matrices, int var1, int var2, float var3)
            {
                visible = true;
                active = (skinList.getSelected() != null&& skinList.getSelected().skinType == "Classic");
                super.render(matrices, var1, var2, var3);
            }
        });

        if(SkinSwapperConfig.showDownloadScreen) {
            this.addDrawableChild(new ButtonWidget(this.skinList.getRowWidth() / 2 + 52 - 40 - 4, 8, 100, 20, new TranslatableText("skin.download_skin"), button -> MinecraftClient.getInstance().openScreen(new DownloadScreen(this)))); //download button
        }
        this.addDrawableChild(new ButtonWidget(this.skinList.getRowWidth()/2 - 52 - 40 -4, 8, 100, 20, new TranslatableText("selectServer.refresh"), button -> { //refresh
            addSkins(folder);
            this.skinList.setSelected(null);
        })); 
    }

    public void render(MatrixStack matrices,  int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        this.skinList.render(matrices, mouseX, mouseY, delta);
        this.previewList.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        try
        {
            skinList.getSelected().drawSkin(this.width - this.previewList.getRowWidth()/2 -16, this.height/2 - 64, matrices);
        }
        catch(Exception e){}

        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        drawCenteredText(matrices,  font, error, this.width - this.previewList.getRowWidth()/2, 40, 0xFFFFFF);
    }

    public void addSkins(final File folder)
    {
        this.skinList.children().clear();
        for (final File fileEntry : folder.listFiles())
        {
            if(fileEntry.getName().contains(".png")&&validateimg(fileEntry))
            {
                this.skinList.children().add(new SkinEntry(fileEntry.getName().substring(0, fileEntry.getName().length()-4),fileEntry,skinList,nativeImage,oldSkin));
            }
        }
    }

    private boolean validateimg(File f) //makes sure it is 64x64 or 64x32
    {
        try
        {
            InputStream inputStream = new FileInputStream(f);
            try
            {
                nativeImage = NativeImage.read((InputStream)inputStream);
            }
            catch(Exception eex)
            {
                inputStream.close();
                return false;
            }
            try
            {   
                Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels tall", new Object[0]);
                inputStream.close();
                oldSkin = false;
                return true;
            }
            
            catch(Exception e)
            {
                try
                {
                    Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(nativeImage.getHeight() == 32, "Must be 32 pixels tall", new Object[0]);
                    
                    inputStream.close();
                    oldSkin = true;
                    return true;
                }
                catch(Exception ex)
                {
                    inputStream.close();
                    return false;
                }
            }
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private void removeEntry(boolean confirmedAction)
    {
        if(confirmedAction)
        {
            skinList.getSelected().deleteSkin(); skinList.children().remove(skinList.getSelected());
            this.skinList.setSelected(null);
        }
        this.client.openScreen(this);
    }

    private void changeSkin(boolean confirmedAction)
    {
        if(confirmedAction)
        {
            if(net.skinchange.changeskin.skinChange.changeSkin(skinList.getSelected().skin_icon, this.skinList.getSelected().skinType, this))
            {
                this.skinList.setSelected(null);
                MinecraftClient.getInstance().openScreen(parent);
            }
            else
            {
                this.client.openScreen(this);
            }
        }
        else
        {
            this.client.openScreen(this);
        }
    }
}