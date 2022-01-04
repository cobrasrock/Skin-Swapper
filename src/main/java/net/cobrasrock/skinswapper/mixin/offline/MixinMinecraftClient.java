package net.cobrasrock.skinswapper.mixin.offline;

import net.cobrasrock.skinswapper.gui.SkinScreen;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @ModifyVariable(at = @At("HEAD"), method = "render", ordinal = 0, argsOnly = true)
    private boolean render(boolean value){
        if(MinecraftClient.getInstance().currentScreen instanceof SkinScreen){
            return false;
        }

        return value;
    }
}
