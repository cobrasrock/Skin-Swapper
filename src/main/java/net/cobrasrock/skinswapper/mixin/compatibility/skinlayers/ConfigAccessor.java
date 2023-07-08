package net.cobrasrock.skinswapper.mixin.compatibility.skinlayers;

import dev.tr7zw.skinlayers.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(value = Config.class, remap = false)
public interface ConfigAccessor {
    @Accessor("enableSkulls")
    void setEnableSkulls(boolean enableSkulls);
}
