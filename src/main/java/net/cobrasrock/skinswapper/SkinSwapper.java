package net.cobrasrock.skinswapper;

import net.cobrasrock.skinswapper.config.MidnightConfig;
import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.ConfigGuiHandler.ConfigGuiFactory;


import java.io.File;

@Mod("skinswapper")
public class SkinSwapper {


    public SkinSwapper() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::clientSetup);

        MidnightConfig.init("skinswapper", SkinSwapperConfig.class);

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiFactory.class, () -> new ConfigGuiFactory((mc, screen) -> MidnightConfig.getScreen(null, "skinswapper")));

        File file = new File("skins");
        file.mkdirs();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        File file = new File("skins");
        file.mkdirs();
    }
}