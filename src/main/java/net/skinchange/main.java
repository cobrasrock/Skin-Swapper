package net.skinchange;

import net.fabricmc.api.ModInitializer;

import java.io.File;

public class main implements ModInitializer
{
    @Override
    public void onInitialize()
    {
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