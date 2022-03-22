package net.cobrasrock.skinswapper.mixin.preview;

import net.cobrasrock.skinswapper.gui.SkinScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PlayerEntityRenderer.class, priority = 1100)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Shadow protected abstract void setModelPose(AbstractClientPlayerEntity player);

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;setModelPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V"), method = "render")
    public void setModelPoseRedirect(PlayerEntityRenderer playerEntityRenderer, AbstractClientPlayerEntity player) {
        if((MinecraftClient.getInstance().currentScreen instanceof SkinScreen)){

            PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = (PlayerEntityModel)this.getModel();

            playerEntityModel.setVisible(true);
            playerEntityModel.hat.visible = isPartVisible(PlayerModelPart.HAT);
            playerEntityModel.jacket.visible = isPartVisible(PlayerModelPart.JACKET);
            playerEntityModel.leftPants.visible = isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
            playerEntityModel.rightPants.visible = isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
            playerEntityModel.leftSleeve.visible = isPartVisible(PlayerModelPart.LEFT_SLEEVE);
            playerEntityModel.rightSleeve.visible = isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            playerEntityModel.sneaking = false;

            playerEntityModel.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
            playerEntityModel.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
        }

        else {
            setModelPose(player);
        }
    }

    private boolean isPartVisible(PlayerModelPart modelPart){
        if(((SkinScreen)MinecraftClient.getInstance().currentScreen).parent instanceof SkinOptionsScreen){
            return(MinecraftClient.getInstance().options.isPlayerModelPartEnabled(modelPart));
        }
        else {
            return true;
        }
    }
}
