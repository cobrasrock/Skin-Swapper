package net.cobrasrock.skinchange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.File;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    @Inject(at = @At("TAIL"), method = "<init>")
    public void MinecraftClient(RunArgs args, CallbackInfo info)
    {
        File file = new File("skins");
        file.mkdirs();
    }
}