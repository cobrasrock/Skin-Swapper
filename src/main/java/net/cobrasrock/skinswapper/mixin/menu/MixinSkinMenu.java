package net.cobrasrock.skinswapper.mixin.menu;

import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.cobrasrock.skinswapper.gui.SkinScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkinOptionsScreen.class)
public class MixinSkinMenu extends GameOptionsScreen {

    public MixinSkinMenu(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo info) {
         //gets button x-coordinate
        int buttonX;
        if (SkinSwapperConfig.skinOptionsButton == SkinSwapperConfig.ModButton.LEFT) {
            buttonX = 25;
        } else if (SkinSwapperConfig.skinOptionsButton == SkinSwapperConfig.ModButton.RIGHT) {
            buttonX = this.width - 125;
        } else if (SkinSwapperConfig.skinOptionsButton == SkinSwapperConfig.ModButton.CENTER) {
            buttonX = (this.width / 2) - 50;
        } else {
            return;
        }

        //draws change skin button
        this.addDrawableChild(new ButtonWidget(buttonX, 6, 100, 20, new TranslatableText("skin.change_skin"), button -> {
            MinecraftClient.getInstance().setScreen(new SkinScreen(this));
        }));
        this.addSelectableChild(new ButtonWidget(buttonX, 6, 100, 20, new TranslatableText("skin.change_skin"), button -> {
            MinecraftClient.getInstance().setScreen(new SkinScreen(this));
        }));
    }

    //hides text which can block the button
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/SkinOptionsScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"), method = "render")
    private void render(MatrixStack matrices, TextRenderer textRenderer, Text text, int centerX, int y, int color){
        if(!(SkinSwapperConfig.skinOptionsButton == SkinSwapperConfig.ModButton.CENTER)) {
            drawCenteredText(matrices, textRenderer, text, centerX, y, color);
        }
    }
}