package net.skinchange.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.Base64;
import java.io.File;
import java.net.URL;
import java.net.UnknownHostException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import net.skinchange.changeskin.skinChange;

public class DownloadScreen extends Screen
{
    private TextFieldWidget downloadField;
    Screen parent;
    String error = "";
    private TextRenderer font;

    public DownloadScreen(Screen scr)
    {
        super(new LiteralText(""));
        parent = scr;
        font = MinecraftClient.getInstance().textRenderer;
    }
    
    @Override
    protected void init()
    {
        this.minecraft.keyboard.enableRepeatEvents(true);

        this.addButton(new ButtonWidget((this.width - ((this.width/2)+4)/2)+6, this.height - 28, 100, 20, "Back", button -> MinecraftClient.getInstance().openScreen(parent)));
        this.addButton(new ButtonWidget((int)(this.width / 2 - 50), 50, 100, 20, "Download", button -> {
            if(download(downloadField.getText()))
            {
                MinecraftClient.getInstance().openScreen(parent);
            }
        }));

        this.downloadField = new TextFieldWidget(this.font, (this.width/2 - 150), 20, 300, 20, this.downloadField, "Username: ");
        this.downloadField.setMaxLength(512);
        this.children.add(this.downloadField);
    }
    
    public void render(int mouseX, int mouseY, float delta)
    {
        super.renderDirtBackground(255);
        this.downloadField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
        drawCenteredString(font, "Username:", this.width/2, 5, 0xFFFFFF);
        drawCenteredString(font, error, this.width/2, this.height/2, 0xFFFFFF);
    }

    @Override
    public void tick()
    {
        this.downloadField.tick();
	}

    public boolean download(String username)
    {
        try
        {
            //act 1 gets uuid
            String a = skinChange.getHTML("https://api.mojang.com/users/profiles/minecraft/" + username);
            JsonObject json = new JsonParser().parse(a).getAsJsonObject();
            String b = json.get("id").getAsString();

            //for file name
            String usernameCAPS = json.get("name").getAsString();

            //act 2 gets session texture value
            a = skinChange.getHTML("https://sessionserver.mojang.com/session/minecraft/profile/" + b);

            json = new JsonParser().parse(a).getAsJsonObject();
            JsonArray c = json.getAsJsonArray("properties");
            for(int i = 0; i<c.size(); i++)
            {
                JsonObject temp = c.get(i).getAsJsonObject();
                b = temp.get("value").getAsString();   
            }

            //act 3 decodes texture
            byte[] decoded = Base64.getDecoder().decode(b);
            b = new String(decoded, "UTF-8");

            //act 4 gets url from texture
            json = new JsonParser().parse(b).getAsJsonObject();
            b = json.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

            //act 5 downloads image
            URL url = new URL(b);
            BufferedImage img = ImageIO.read(url);
            File file = new File("skins\\" + usernameCAPS + ".png");
            ImageIO.write(img, "png", file);
            return true;
        }
        catch(UnknownHostException e)
        {
            error = ("No Internet Connection");
            return false;
        }
        catch(Exception e)
        {
            error = ("Invalid username");
            return false;
        }
    }
}