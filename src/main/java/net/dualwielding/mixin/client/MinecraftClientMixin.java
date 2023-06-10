package net.dualwielding.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.dualwielding.access.PlayerAccess;
import net.dualwielding.network.PlayerAttackPacket;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;
    @Shadow
    @Nullable
    public HitResult crosshairTarget;
    @Shadow
    private int itemUseCooldown;
    @Unique
    private int secondAttackCooldown;

    @Inject(method = "Lnet/minecraft/client/MinecraftClient;tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V"))
    public void tickMixin(CallbackInfo info) {
        if (this.secondAttackCooldown > 0) {
            --this.secondAttackCooldown;
        }
    }

    @Inject(method = "doItemUse", at = @At(value = "HEAD"), cancellable = true)
    private void doItemUseMixin(CallbackInfo info) {
        Item offHandItem = player.getOffHandStack().getItem();
        Item mainHandItem = player.getMainHandStack().getItem();

        if (player != null && !player.isSpectator() && (offHandItem instanceof SwordItem || offHandItem instanceof MiningToolItem)
                && (mainHandItem instanceof SwordItem || mainHandItem instanceof MiningToolItem) && PlayerAttackPacket.medievalWeaponsDoubleHanded(player.getOffHandStack(), mainHandItem)) {
            if (this.secondAttackCooldown <= 0) {
                if (this.crosshairTarget != null && !this.player.isRiding()) {
                    switch (this.crosshairTarget.getType()) {
                    case ENTITY:
                        // Client
                        ((PlayerAccess) player).resetLastDualOffhandAttackTicks();
                        ((PlayerAccess) this.player).attackOffhand(((EntityHitResult) this.crosshairTarget).getEntity());

                        // Server
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(PlayerAttackPacket.attackPacket(((EntityHitResult) this.crosshairTarget).getEntity()));
                        break;
                    case BLOCK:
                        BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;
                        BlockPos blockPos = blockHitResult.getBlockPos();
                        if (!player.getWorld().getBlockState(blockPos).isAir()) {
                            this.interactionManager.interactBlock(player, Hand.OFF_HAND, blockHitResult);
                            break;
                        }
                    case MISS:
                        if (this.interactionManager.hasLimitedAttackSpeed()) {
                            this.secondAttackCooldown = 10;
                        }
                        ((PlayerAccess) player).resetLastDualOffhandAttackTicks();
                    }
                    this.itemUseCooldown = 4;
                    this.player.swingHand(Hand.OFF_HAND);
                    info.cancel();
                }
            } else {
                info.cancel();
            }
        }
    }

}