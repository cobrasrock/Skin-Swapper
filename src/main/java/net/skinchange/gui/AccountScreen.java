package net.skinchange.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.skinchange.changeskin.skinChange;

import java.util.Objects;

public class AccountScreen extends Screen
{
    public TextFieldWidget usernameField;
    public static PasswordWidget passwordField;
    public String error;
    private Screen parent;
    TextRenderer font;

    public AccountScreen(Screen scr)
    {
        super(new LiteralText(""));
        parent = scr;
        error = ""; 
        font = MinecraftClient.getInstance().textRenderer;
    }
    
    @Override
    protected void init()
    {
        Objects.requireNonNull(this.client).keyboard.enableRepeatEvents(true);

        this.addButton(new ButtonWidget((this.width - ((this.width/2)+4)/2)+6, this.height - 28, 100, 20, new TranslatableText("gui.back"), button -> MinecraftClient.getInstance().openScreen(parent)));
        this.addButton(new ButtonWidget((int)(this.width / 2 - 50), 100, 100, 20, new TranslatableText("skin.login"), button ->{
            if(skinChange.authenticate(this.usernameField.getText(), this.passwordField.password, this))
            {          
                MinecraftClient.getInstance().openScreen(parent);
            }
        }));

        this.usernameField = new TextFieldWidget(this.font, (this.width/2 - 150), 20, 300, 20, this.usernameField, new TranslatableText("username"));
        this.usernameField.setMaxLength(512);
        this.children.add(this.usernameField);

        this.passwordField = new PasswordWidget(this.font, (this.width/2 - 150), 60, 300, 20, "Password: ");
        this.passwordField.setMaxLength(512);
        this.children.add(this.passwordField);
    }
    
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        this.usernameField.render(matrices, mouseX, mouseY, delta);
        this.passwordField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredString(matrices, font, I18n.translate("skin.username_email"), this.width/2, 6, 0xFFFFFF);
        drawCenteredString(matrices, font, I18n.translate("skin.password"), this.width/2, 46, 0xFFFFFF);
        drawCenteredString(matrices, font, error, this.width/2, 85, 0xFFFFFF);
    }

    @Override
    public void tick()
    {
        this.usernameField.tick();
        this.passwordField.tick();
	}
}