package net.cobrasrock.skinswapper.mixin.menu;

import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.cobrasrock.skinswapper.gui.SkinScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SelectWorldScreen.class)
public class MixinSingleplayerMenu extends Screen {
    public MixinSingleplayerMenu(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "init")
    private void init(CallbackInfo info) {
        if(SkinSwapperConfig.offlineMode || SkinSwapperConfig.offlineModeToggle) {
            //gets button x-coordinate
            int buttonX;
            int buttonY;

            if (SkinSwapperConfig.singleplayerButton == SkinSwapperConfig.ModButton.LEFT) {
                buttonX = 6;
                buttonY = 6;
            } else if (SkinSwapperConfig.singleplayerButton == SkinSwapperConfig.ModButton.RIGHT) {
                buttonX = this.width - 106;
                buttonY = 6;
            } else if (SkinSwapperConfig.singleplayerButton == SkinSwapperConfig.ModButton.CENTER){
                buttonX = (this.width / 2) - 50;
                buttonY = 0;
            } else {
                return;
            }

            //draws change skin button
            this.addDrawableChild(ButtonWidget.builder(Text.translatable("skin.change_skin"), button -> MinecraftClient.getInstance().setScreen(new SkinScreen(this)))
                    .dimensions(buttonX, 6, 100, 20)
                    .build());

            //this.addDrawableChild(new ButtonWidget(buttonX, buttonY, 100, 20, Text.translatable("skin.change_skin"), button -> {
               // MinecraftClient.getInstance().setScreen(new SkinScreen(this));
            //}));
            //this.addSelectableChild(new ButtonWidget(buttonX, buttonY, 100, 20, Text.translatable("skin.change_skin"), button -> {
                //MinecraftClient.getInstance().setScreen(new SkinScreen(this));
            //}));
        }
    }

    //hides singleplayer text which can block the button
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/SelectWorldScreen;drawCenteredTextWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"), method = "render")
    private void render(MatrixStack matrices, TextRenderer textRenderer, Text text, int centerX, int y, int color){
        if(!(SkinSwapperConfig.singleplayerButton == SkinSwapperConfig.ModButton.CENTER)) {
            drawCenteredTextWithShadow(matrices, textRenderer, text, centerX, y, color);
        }
    }
}