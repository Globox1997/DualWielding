package net.dualwielding.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.Environment;
import net.dualwielding.access.PlayerAccess;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Shadow
    private float equipProgressOffHand;
    @Shadow
    private float equipProgressMainHand;
    @Shadow
    private ItemStack mainHand;
    @Shadow
    private float prevEquipProgressMainHand;
    @Shadow
    private float prevEquipProgressOffHand;

    @Shadow
    @Final
    @Mutable
    private MinecraftClient client;

    @Shadow
    private ItemStack offHand;

    private float equipOffhand;
    private boolean isOffhandAttack;

    public HeldItemRendererMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(method = "updateHeldItems", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F", ordinal = 3, shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void updateHeldItemsMixin(CallbackInfo info, ClientPlayerEntity clientPlayerEntity, ItemStack itemStack,
            ItemStack itemStack2) {
        float o = ((PlayerAccess) clientPlayerEntity).getAttackCooldownProgressOffhand(1.0F);
        if (o < 0.1F) {
            this.isOffhandAttack = true;
          //  this.equipOffhand = 1.0F;
        }
        if (this.isOffhandAttack) {
            if (this.equipProgressMainHand >= 1.0F) {
                this.isOffhandAttack = false;
            }
            this.equipOffhand += MathHelper.clamp((this.offHand == itemStack2 ? o * o * o : 0.0F) - this.equipOffhand,
                    -0.4F, 0.4F);
            this.equipProgressOffHand = this.equipOffhand;
        }
    }

    @Inject(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    public void updateHeldMainhandMixin(CallbackInfo info) {
        float o = ((PlayerAccess) client.player).getAttackCooldownProgressOffhand(1.0F);
        if (o < 0.9F && o > 0.15F) {
            this.offHand = new ItemStack(Items.AIR);
        }

    }

    // @ModifyVariable(method = "updateHeldItems", at = @At(value = "FIELD", target
    // =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;equipProgressOffHand:F",ordinal
    // = 0), index = -1, print = true)
    // private float renderItemOffhandMixin(float original) {
    // // PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    // // if (this.offHandAttack) {
    // // SwordItem swordItem = (SwordItem)
    // // playerEntity.getOffHandStack().getItem();
    // // // return swordItem.getAttackDamage() + 1F;
    // // } else
    // System.out.println(original);
    // return 0.0F;
    // }

    // @ModifyVariable(method =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
    // at = @At(value = "INVOKE_ASSIGN", target =
    // "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F",ordinal = 4),index =
    // -1,print = true)
    // private float renderItemMainHandMixin(float original) {
    // // PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    // // if (this.offHandAttack) {
    // // SwordItem swordItem = (SwordItem)
    // playerEntity.getOffHandStack().getItem();
    // // return swordItem.getAttackDamage() + 1F;
    // // } else
    // System.out.println(original);
    // return 1.0F;
    // }
    // Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V

    // net/minecraft/client/render/item/HeldItemRenderer.renderFirstPersonItem
    // (Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V
    // Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V

    // @ModifyVariable(method =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
    // at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",ordinal
    // = 0,shift = Shift.BEFORE),ordinal = 4)
    // private float renderItemMainHandMixin(float original) {
    // // PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    // // if (this.offHandAttack) {
    // // SwordItem swordItem = (SwordItem)
    // playerEntity.getOffHandStack().getItem();
    // // return swordItem.getAttackDamage() + 1F;
    // // } else
    // System.out.println(original);
    // return 0.0F;
    // }

    // @ModifyVariable(method =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
    // at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F",ordinal = 3,shift =
    // Shift.AFTER),index = -1,print = true)
    // private float renderItemMainHandMixin(float original) {
    // // PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    // // if (this.offHandAttack) {
    // // SwordItem swordItem = (SwordItem)
    // playerEntity.getOffHandStack().getItem();
    // // return swordItem.getAttackDamage() + 1F;
    // // } else
    // System.out.println(original);
    // return 0.0F;
    // }

    // (Lnet/minecraft/client/render/item/HeldItemRenderer;Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V

    // //Works only partly
    // @Redirect(method =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
    // at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",ordinal
    // = 0))
    // private void renderItemMainHandMixin(HeldItemRenderer
    // heldItemRenderer,AbstractClientPlayerEntity player, float tickDelta, float
    // pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress,
    // MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    // if(this.isOffhandAttack){
    // System.out.println(equipProgress);
    // equipProgress = 0.0F;
    // }
    // this.renderFirstPersonItem(player, tickDelta, pitch, Hand.MAIN_HAND,
    // swingProgress, this.mainHand, equipProgress, matrices, vertexConsumers,
    // light);
    // }

    // Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V
    // @Redirect(method =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
    // at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
    // ordinal = 1))
    // private void renderItemMainHandMixin(HeldItemRenderer heldItemRenderer,
    // AbstractClientPlayerEntity player,
    // float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item,
    // float equipProgress,
    // MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    // if (this.isOffhandAttack) {
    // // m = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand,
    // this.equipProgressOffHand);
    // // System.out.println(equipProgress);
    // equipProgress = 1.0F - MathHelper.lerp(tickDelta, this.equipOffhand,
    // this.equipProgressOffHand);
    // }
    // this.renderFirstPersonItem(player, tickDelta, pitch, Hand.OFF_HAND,
    // swingProgress, this.mainHand,
    // equipProgress, matrices, vertexConsumers, light);
    // }

    // @Shadow
    // private void renderFirstPersonItem(AbstractClientPlayerEntity player, float
    // tickDelta, float pitch, Hand hand,
    // float swingProgress, ItemStack item, float equipProgress, MatrixStack
    // matrices,
    // VertexConsumerProvider vertexConsumers, int light) {
    // }

    // TEst
    // @Inject(method = "updateHeldItems", at = @At(value = "TAIL"))
    // public void updateHeldMainHandMixin(CallbackInfo info) {
    // if (this.isOffhandAttack&& this.equipProgressMainHand < 0.1F) {
    // this.equipProgressMainHand = 1.0F;
    // }
    // // System.out.println(this.equipProgressMainHand);
    // }

    // @Inject(method = "updateHeldItems", at = @At(value = "TAIL"), locals =
    // LocalCapture.CAPTURE_FAILSOFT)
    // public void testMixin(CallbackInfo info, ClientPlayerEntity
    // clientPlayerEntity, ItemStack itemStack,
    // ItemStack itemStack2) {
    // if (!clientPlayerEntity.isRiding() && ((PlayerAccess)
    // clientPlayerEntity).isOffhandAttack()) {
    // // System.out.println(this.equipProgressMainHand);
    // this.equipProgressMainHand = 1.0F;
    // // this.mainHand = itemStack;
    // }

    // }

    // public void updateHeldItemsMixin() {
    // ClientPlayerEntity clientPlayerEntity = this.client.player;
    // ItemStack itemStack = clientPlayerEntity.getMainHandStack();
    // ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
    // if (ItemStack.areEqual(this.mainHand, itemStack)) {
    // this.mainHand = itemStack;
    // }

    // if (ItemStack.areEqual(this.offHand, itemStack2)) {
    // this.offHand = itemStack2;
    // }

    // if (clientPlayerEntity.isRiding()) {
    // this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand -
    // 0.4F, 0.0F, 1.0F);
    // this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand -
    // 0.4F, 0.0F, 1.0F);
    // } else {
    // float o = ((PlayerAccess)
    // clientPlayerEntity).getAttackCooldownProgressOffhand(1.0F);
    // // float f = clientPlayerEntity.getAttackCooldownProgress(1.0F);
    // this.equipProgressMainHand = 1.0F;
    // // this.equipProgressMainHand += MathHelper
    // // .clamp((this.mainHand == itemStack ? f * f * f : 0.0F) -
    // // this.equipProgressMainHand, -0.4F, 0.4F);
    // // this.equipProgressOffHand += MathHelper
    // // .clamp((float) (this.offHand == itemStack2 ? 1 : 0) -
    // // this.equipProgressOffHand, -0.4F, 0.4F);
    // this.equipProgressOffHand += MathHelper
    // .clamp((this.offHand == itemStack2 ? o * o * o : 0.0F) -
    // this.equipProgressOffHand, -0.4F, 0.4F);
    // }

    // if (this.equipProgressMainHand < 0.1F) {
    // this.mainHand = itemStack;
    // }

    // if (this.equipProgressOffHand < 0.1F) {
    // this.offHand = itemStack2;
    // }

    // if (this.equipProgressOffHand >= 1.0F) {
    // this.isOffhandAttack = false;
    // }
    // this.prevEquipProgressMainHand = this.equipProgressMainHand;
    // this.prevEquipProgressOffHand = this.equipProgressOffHand;

    // }

}
