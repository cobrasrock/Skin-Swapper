package net.cobrasrock.skinswapper.mixin.menu;

import net.cobrasrock.skinswapper.config.SkinSwapperConfig;
import net.cobrasrock.skinswapper.gui.SkinScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MixinMultiplayerMenu extends Screen {
	public MixinMultiplayerMenu(Text title) {
		super(title);
	}

	@Inject(at = @At("RETURN"), method = "init")
	private void init(CallbackInfo info) {
		//gets button x-coordinate
		int buttonX;
		if(SkinSwapperConfig.multiplayerButton == SkinSwapperConfig.ModButton.LEFT){
			buttonX = 25;
		} else if(SkinSwapperConfig.multiplayerButton == SkinSwapperConfig.ModButton.RIGHT){
			buttonX = this.width - 125;
		} else if(SkinSwapperConfig.multiplayerButton == SkinSwapperConfig.ModButton.CENTER){
			buttonX = (this.width/2) - 50;
		} else {
			return;
		}

		//draws change skin button
		this.addDrawableChild(ButtonWidget.builder(Text.translatable("skin.change_skin"), button -> MinecraftClient.getInstance().setScreen(new SkinScreen(this)))
				.dimensions(buttonX, 6, 100, 20)
				.build());


		//this.addDrawableChild(new ButtonWidget(buttonX, 6, 100, 20, Text.translatable("skin.change_skin"), button ->{
			//MinecraftClient.getInstance().setScreen(new SkinScreen(this));
		//}));
		//this.addSelectableChild(new ButtonWidget(buttonX, 6, 100, 20, Text.translatable("skin.change_skin"), button ->{
			//MinecraftClient.getInstance().setScreen(new SkinScreen(this));
		//}));
	}

	//hides multiplayer text which can block the button
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"), method = "render")
	private void render(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int y, int color){
		if(!(SkinSwapperConfig.multiplayerButton == SkinSwapperConfig.ModButton.CENTER)){
			context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		}
	}
}