package net.cobrasrock.skinswapper.mixin.compatibility.skinlayers;

import dev.tr7zw.skinlayers.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(Config.class)
public interface ConfigAccessor {
    @Accessor("enableSkulls")
    void setEnableSkulls(boolean enableSkulls);
}
