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

import java.io.File;

public class SkinScreen extends Screen {
    final File folder;
    private final Screen parent;
    private static SkinListWidget skinList;
    public String error;

    public SkinScreen(Screen scr) {
        super(new LiteralText(""));
        this.parent = scr;
        this.folder = new File("skins");
    }

    @Override
    protected void init() {

        //lists skins
        skinList = new SkinListWidget(this.client, this.width/2, this.height, 36, this.height - 52, 36, this);
        skinList.setLeftPos(0);
        this.addSelectableChild(skinList);

        addSkins(folder); //adds skins

        this.addDrawableChild(new ButtonWidget((this.width - (this.width/4)) + 2, this.height - 24, 100, 20, new TranslatableText("gui.back"), button -> MinecraftClient.getInstance().openScreen(parent))); //back

        this.addDrawableChild(new ButtonWidget(((this.width - (this.width/4)) - 100 - 2), this.height - 24, 100, 20, new TranslatableText("skin.change_skin"), button -> //change skin
        {
            this.client.openScreen(new ConfirmScreen(this::changeSkin, new LiteralText(I18n.translate("skin.are_you_sure")), new LiteralText(I18n.translate("skin.changeto") + " '" + skinList.getSelected().fname + "'"), new TranslatableText("gui.yes"), new TranslatableText("gui.cancel")));
        }) {
            @Override
            public void render(MatrixStack matrices, int var1, int var2, float var3) {
                visible = true;
                active = (skinList.getSelected() != null);
                super.render(matrices, var1, var2, var3);
            }
        });

        this.addDrawableChild(new ButtonWidget(this.width/4 + 2, this.height - 24, 100, 20, new TranslatableText("skin.open_folder"), button -> Util.getOperatingSystem().open(new File("skins")))); //open skin folder

        this.addDrawableChild(new ButtonWidget(this.width/4 - 100 - 2, this.height - 24, 100, 20, new TranslatableText("skin.delete_skin"), button -> //delete skin
        {
            this.client.openScreen(new ConfirmScreen(this::removeEntry, new LiteralText(I18n.translate("skin.are_you_sure_remove")), new LiteralText("'" + skinList.getSelected().fname + "' " + I18n.translate("skin.long")), new TranslatableText("selectWorld.delete"), new TranslatableText("gui.cancel")));
        }) {
            @Override
            public void render(MatrixStack matrices, int var1, int var2, float var3) {
                visible = true;
                active = (skinList.getSelected() != null);
                super.render(matrices, var1, var2, var3);
            }
        });

        this.addDrawableChild(new ButtonWidget(this.width/4 + 2, this.height - 48, 100, 20, new TranslatableText("skin.classic"), button -> //classic select button
        {
            skinList.getSelected().toggleSkinType();
        }) {
            @Override
            public void render(MatrixStack matrices, int var1, int var2, float var3) {
                visible = true;
                active = (skinList.getSelected() != null && skinList.getSelected().skinType.equals("slim"));
                super.render(matrices, var1, var2, var3);
            }
        });

        this.addDrawableChild(new ButtonWidget(this.width/4 - 100 - 2, this.height - 48, 100, 20, new TranslatableText("skin.slim"), button -> //slim select button
        {
            skinList.getSelected().toggleSkinType();
        }) {
            @Override
            public void render(MatrixStack matrices, int var1, int var2, float var3) {
                visible = true;
                active = (skinList.getSelected() != null && skinList.getSelected().skinType.equals("classic"));
                super.render(matrices, var1, var2, var3);
            }
        });

        if (SkinSwapperConfig.showDownloadScreen) {
            this.addDrawableChild(new ButtonWidget(this.width/4 + 2, 8, 100, 20, new TranslatableText("skin.download_skin"), button -> MinecraftClient.getInstance().openScreen(new DownloadScreen(this)))); //download button
        }
        this.addDrawableChild(new ButtonWidget(this.width/4 - 100 - 2, 8, 100, 20, new TranslatableText("selectServer.refresh"), button -> { //refresh
            addSkins(folder);
            skinList.setSelected(null);
        }));
    }

    public void render(MatrixStack matrices,  int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        skinList.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);

        if(SkinSwapperConfig.displayType == SkinSwapperConfig.DisplayType.LEGACY) {
            try {
                skinList.getSelected().drawSkin(this.width - skinList.getRowWidth() / 2 - 16, this.height / 2 - 64, matrices);
            } catch (Exception e) {
            }
        } else {
            if(skinList.getSelected() != null){
                SkinUtils.drawPlayer(this.width - (this.width/4), this.height - 36, 92, mouseX, mouseY);
            }
        }

        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        drawCenteredText(matrices, font, error, this.width - this.width/4, 12, 0xFFFFFF);
    }

    public void addSkins(final File folder) {
        NativeImage nativeImage;
        boolean oldSkin;

        skinList.children().clear();
        for (final File fileEntry : folder.listFiles())
        {
            nativeImage = SkinUtils.toNativeImage(fileEntry);

            if(nativeImage != null){

                //checks dimensions of file
                if(nativeImage.getWidth() == 64 && nativeImage.getHeight() == 32){
                    oldSkin = true;
                } else if(nativeImage.getWidth() == 64 && nativeImage.getHeight() == 64){
                    oldSkin = false;
                } else {
                    continue;
                }
                skinList.children().add(new SkinEntry(fileEntry.getName().substring(0, fileEntry.getName().length()-4),fileEntry,skinList,nativeImage,oldSkin));


            }
        }
    }

    private void removeEntry(boolean confirmedAction)
    {
        if(confirmedAction)
        {
            skinList.getSelected().deleteSkin(); skinList.children().remove(skinList.getSelected());
            skinList.setSelected(null);
        }
        this.client.openScreen(this);
    }

    public static SkinEntry getSelected(){
        return skinList.getSelected();
    }

    private void changeSkin(boolean confirmedAction)
    {
        if(confirmedAction)
        {
            if(net.skinchange.changeskin.SkinChange.changeSkin(skinList.getSelected().skin_file, skinList.getSelected().skinType, this))
            {
                skinList.setSelected(null);
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