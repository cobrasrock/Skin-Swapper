package net.skinchange.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.LiteralText;
//import net.minecraft.util.SystemUtil;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Util;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.lang3.Validate;

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
        this.file = new File("config\\skinchange\\data.txt");
    }
    
    @Override
    protected void init()
    {
        //does nothing but darken screen
        this.previewList = new SkinListWidget(this.minecraft, this.width/2 + 4, 0, 36 ,this.height-36, 36);
        this.previewList.setLeftPos(this.width/2+4);
        this.children.add(this.previewList);

        //lists skins
        this.skinList = new SkinListWidget(this.minecraft, this.width/2 - 4, this.height, 36 ,this.height-52, 36);
        this.skinList.setLeftPos(0);
        this.children.add(this.skinList);

        addSkins(folder); //adds skins

        this.addButton(new ButtonWidget(this.width - this.previewList.getRowWidth()/2, this.height - 28, 100, 20, "Back", button -> MinecraftClient.getInstance().openScreen(parent))); //back
        this.addButton(new ButtonWidget(this.width - this.previewList.getRowWidth()/2, 8, 100, 20, "Log In", button -> //log in
        {
            this.minecraft.openScreen(new AccountScreen(this));
        })
        {
            @Override
            public void render(int var1, int var2, float var3)
            {
                visible = (file.length() == 0);
                active = (file.length() == 0);
                super.render(var1, var2, var3);
            }
        });

        this.addButton(new ButtonWidget(this.width - this.previewList.getRowWidth()/2, 8, 100, 20, "Log Out", button -> //log out
        {
            try{
                PrintWriter writer = new PrintWriter(file); //clears auth file
                writer.print("");
                writer.close();
            }
            catch(Exception e){}
        })
        {
            @Override
            public void render(int var1, int var2, float var3)
            {
                visible = (file.length() != 0);
                active = (file.length() != 0);
                super.render(var1, var2, var3);
            }
        });

        this.addButton(new ButtonWidget(this.width - this.previewList.getRowWidth()/2 - 52 - 52, this.height - 28, 100, 20, "Change Skin", button -> //change skin
        {
            this.minecraft.openScreen(new ConfirmScreen(this::changeSkin, new LiteralText("Are you sure you want to change your skin?"), new LiteralText("Your current skin will be changed to '" + skinList.getSelected().fname + "'"), "Yes", "Cancel"));
        })
        {
            @Override
            public void render(int var1, int var2, float var3)
            {
                visible = true;
                active = (skinList.getSelected() != null)&&(file.length() != 0);
                super.render(var1, var2, var3);
            }
        });

        this.addButton(new ButtonWidget(this.skinList.getRowWidth()/2 - 52 - 40 -4, this.height - 24, 100, 20, "Open Skin Folder", button -> Util.getOperatingSystem().open(new File("skins")))); //open skin folder

        this.addButton(new ButtonWidget(this.skinList.getRowWidth()/2 + 52 - 40 -4, this.height - 24, 100, 20, "Delete Skin", button -> //delete skin
        {
            this.minecraft.openScreen(new ConfirmScreen(this::removeEntry, new LiteralText("Are you sure you want to remove this skin?"), new LiteralText("'" + skinList.getSelected().fname + "' will be lost forever! (A long time!)"), "Delete", "Cancel"));
        })
        {
            @Override
            public void render(int var1, int var2, float var3)
            {
                visible = true;
                active = (skinList.getSelected() != null);
                super.render(var1, var2, var3);
            }
        });

        this.addButton(new ButtonWidget(this.skinList.getRowWidth()/2 - 52 - 40-4, this.height - 48, 100, 20, "Skin type: Classic", button -> //classic select button
        {skinList.getSelected().toggleSkinType();})
        {
            @Override
            public void render(int var1, int var2, float var3)
            {
                visible = true;
                active = (skinList.getSelected() != null&& skinList.getSelected().skinType == "Slim");
                super.render(var1, var2, var3);
            }
        });

        this.addButton(new ButtonWidget(this.skinList.getRowWidth()/2 + 52 - 40-4, this.height - 48, 100, 20, "Skin type: Slim", button -> //slim select button
        {skinList.getSelected().toggleSkinType();})
        {
            @Override
            public void render(int var1, int var2, float var3)
            {
                visible = true;
                active = (skinList.getSelected() != null&& skinList.getSelected().skinType == "Classic");
                super.render(var1, var2, var3);
            }
        });

        this.addButton(new ButtonWidget(this.skinList.getRowWidth()/2 - 52 - 40 -4, 8, 100, 20, "Download Skin", button -> MinecraftClient.getInstance().openScreen(new DownloadScreen(this)))); //download button

        this.addButton(new ButtonWidget(this.skinList.getRowWidth()/2 + 52 - 40 -4, 8, 100, 20, "Refresh", button -> { //refresh
            addSkins(folder);
            this.skinList.setSelected(null);
        })); 
    }

    public void render(int mouseX, int mouseY, float delta)
    {
        super.renderDirtBackground(0);
        this.skinList.render(mouseX, mouseY, delta);
        this.previewList.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
        try
        {
            skinList.getSelected().drawSkin((this.width - this.previewList.getRowWidth()/2)-16, this.height/2 - 64);
        }
        catch(Exception e){}

        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        drawCenteredString(font, error, this.width - this.previewList.getRowWidth()/2, 40, 0xFFFFFF);
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
        this.minecraft.openScreen(this);
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
                this.minecraft.openScreen(this);
            }
        }
        else
        {
            this.minecraft.openScreen(this);
        }
    }
}