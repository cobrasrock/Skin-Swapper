  
package net.skinchange.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.skinchange.gui.AccountScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(TextFieldWidget.class)
public abstract class MixinTextField{

	@Inject(at = @At("RETURN"), method = "charTyped")
	private void charTyped(CallbackInfoReturnable info)
	{
		try 
		{
			AccountScreen.passwordField.update();
		}
		catch(Exception e){}
	}
	@Inject(at = @At("RETURN"), method = "keyPressed")
	private void keyPressed(CallbackInfoReturnable info)
	{
		try
		{
			AccountScreen.passwordField.update();
		}
		catch(Exception e){}
	}
}