package net.skinchange.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.client.font.TextRenderer;

import net.skinchange.changeskin.skinChange;

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
        this.minecraft.keyboard.enableRepeatEvents(true);

        this.addButton(new ButtonWidget((this.width - ((this.width/2)+4)/2)+6, this.height - 28, 100, 20, "Back", button -> MinecraftClient.getInstance().openScreen(parent)));
        this.addButton(new ButtonWidget((int)(this.width / 2 - 50), 100, 100, 20, "Login", button ->{
            if(skinChange.authenticate(this.usernameField.getText(), this.passwordField.password, this))
            {          
                MinecraftClient.getInstance().openScreen(parent);
            }
        }));

        this.usernameField = new TextFieldWidget(this.font, (this.width/2 - 150), 20, 300, 20, this.usernameField, "Username: ");
        this.usernameField.setMaxLength(512);
        this.children.add(this.usernameField);

        this.passwordField = new PasswordWidget(this.font, (this.width/2 - 150), 60, 300, 20, "Password: ");
        this.passwordField.setMaxLength(512);
        this.children.add(this.passwordField);
    }
    
    public void render(int mouseX, int mouseY, float delta)
    {
        super.renderDirtBackground(255);
        this.usernameField.render(mouseX, mouseY, delta);
        this.passwordField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);

        drawCenteredString(font, "Username/Email", this.width/2, 6, 0xFFFFFF);
        drawCenteredString(font, "Password", this.width/2, 46, 0xFFFFFF);
        drawCenteredString(font, error, this.width/2, 85, 0xFFFFFF);
    }

    @Override
    public void tick()
    {
        this.usernameField.tick();
        this.passwordField.tick();
	}
}