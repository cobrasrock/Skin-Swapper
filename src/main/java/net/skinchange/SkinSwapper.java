package net.skinchange;

import net.fabricmc.api.ModInitializer;
import net.skinchange.config.MidnightConfig;
import net.skinchange.config.SkinSwapperConfig;

import java.io.File;

public class SkinSwapper implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        File file = new File("skins");
        file.mkdirs();
        MidnightConfig.init("skinswapper", SkinSwapperConfig.class);
    }
}