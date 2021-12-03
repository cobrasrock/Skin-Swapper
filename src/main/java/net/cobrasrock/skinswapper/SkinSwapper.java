package net.cobrasrock.skinswapper;

import net.fabricmc.api.ModInitializer;
import net.cobrasrock.skinswapper.config.MidnightConfig;
import net.cobrasrock.skinswapper.config.SkinSwapperConfig;

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