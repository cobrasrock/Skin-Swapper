package net.cobrasrock.skinchange;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("skinswapper")
public class Main {
    public Main() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
