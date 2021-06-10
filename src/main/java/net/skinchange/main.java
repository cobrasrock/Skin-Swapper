package net.skinchange;

import net.fabricmc.api.ModInitializer;
import net.skinchange.config.MidnightConfig;
import net.skinchange.config.SkinSwapperConfig;

import java.io.File;

public class main implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        MidnightConfig.init("skinswapper", SkinSwapperConfig.class);

        File file = new File("config" + File.separator + "skinchange");
        file.mkdirs();

        file = new File("config"+ File.separator + "skinchange" + File.separator + "data.txt");
        try
        {
            file.createNewFile();
        }catch(Exception e){}

        file = new File("skins");
        file.mkdirs();
    }

}