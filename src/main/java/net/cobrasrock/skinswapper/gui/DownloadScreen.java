package net.cobrasrock.skinswapper.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.cobrasrock.skinswapper.changeskin.SkinChange;
import org.lwjgl.glfw.GLFW;

import java.net.UnknownHostException;
import java.util.Objects;

public class DownloadScreen extends Screen {
    private TextFieldWidget downloadField;
    Screen parent;
    String error = "";
    private TextRenderer font;

    public DownloadScreen(Screen scr) {
        super(Text.translatable("skin.download_skin"));
        parent = scr;
        font = MinecraftClient.getInstance().textRenderer;
    }
    
    @Override
    protected void init() {
        Objects.requireNonNull(this.client).keyboard.setRepeatEvents(true);

        //back button
        this.addDrawableChild(new ButtonWidget((this.width - (this.width/4)) + 2, this.height - 24, 100, 20, Text.translatable("gui.back"), button -> MinecraftClient.getInstance().setScreen(parent)));

        //download skin button
        this.addDrawableChild(new ButtonWidget((int)(this.width / 2 - 50), 50, 100, 20, Text.translatable("skin.download"), button -> {
            if(download(downloadField.getText())) {
                MinecraftClient.getInstance().setScreen(parent);
            }
        }));

        //download text field
        this.downloadField = new TextFieldWidget(this.font, (this.width/2 - 150), 20, 300, 20, this.downloadField, Text.translatable("skin.username"));
        this.downloadField.setMaxLength(16);
        this.addSelectableChild(this.downloadField);
    }
    
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        this.downloadField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, font, I18n.translate("skin.username"), this.width/2, 5, 0xFFFFFF);
        drawCenteredText(matrices, font, error, this.width/2, this.height/2, 0xFFFFFF);
    }

    @Override
    public void tick() {
        this.downloadField.tick();
	}

    /**
     * Downloads a skin to "skins" folder.
     * @param username The username of the skin.
     * @return Whether or not the skin was downloaded.
     */
    public boolean download(String username) {
        try {
            SkinChange.downloadSkin(username);
            return true;
        }
        catch(UnknownHostException e) {
            error = (I18n.translate("skin.no_internet"));
            return false;
        }
        catch(Exception e) {
            error = (I18n.translate("skin.invalid_username"));
            return false;
        }
    }

    //taken from yarn mappings for direct connect screen, detects when "enter" key is pressed and downloads skin.
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if (this.getFocused() != this.downloadField || keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            if(download(downloadField.getText())) {
                MinecraftClient.getInstance().setScreen(parent);
            }
            return true;
        }
    }
}