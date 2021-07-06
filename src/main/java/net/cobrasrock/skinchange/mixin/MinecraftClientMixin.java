package net.cobrasrock.skinchange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.File;
import java.io.PrintWriter;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    @Inject(at = @At("TAIL"), method = "<init>")
    public void MinecraftClient(RunArgs args, CallbackInfo info)
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

        try{
            PrintWriter writer = new PrintWriter("config" + File.separator + "skinchange" + File.separator + "data.txt", "UTF-8");
            writer.println(args.network.session.getUsername());
            writer.println(args.network.session.getAccessToken());
            writer.close();
        }catch (Exception e){}
    }
}