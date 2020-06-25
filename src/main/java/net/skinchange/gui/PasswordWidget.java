package net.skinchange.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;

public class PasswordWidget extends TextFieldWidget
{
    public String password = "";

    public void update()
    {
        String res = getText();
        if (res.length() != password.length())
        {
            if (res.length() > password.length())
            {
                password += res.replace(""+'\u25CF',"");
                this.updateText();
            }

            if (res.length() < password.length())
            {
                int subtract = password.length() - res.length();
                password = password.substring(0, password.length() - subtract);
                this.updateText();       
            }
        }
    }

    public PasswordWidget(TextRenderer f, int px, int py, int x, int y, String s)
    {
        super(f, px, py, x, y, new LiteralText(s));
        this.setMaxLength(512);
        updateText();
    }

    private void updateText()
    {
        String result = "";
        for (int i = 0; i < password.length(); i++)
        {
            result += '\u25CF';
        }
        super.setText(result);
    }
}