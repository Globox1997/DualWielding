package net.dualwielding.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    private int secondAttackCooldown;
    private boolean attackedOffhand;

    @Inject(method = "Lnet/minecraft/client/MinecraftClient;tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V"))
    public void tickMixin(CallbackInfo info) {
        if (this.secondAttackCooldown > 0) {
            --this.secondAttackCooldown;
        }
        if (this.attackedOffhand) {
            this.itemUseCooldown = 4;
        }
    }

    @Inject(method = "doItemUse", at = @At(value = "HEAD"), cancellable = true)
    private void doItemUseMixin(CallbackInfo info) {
        if (player != null && !player.isSpectator()
                && (player.getOffHandStack().getItem() instanceof SwordItem
                        || player.getOffHandStack().getItem() instanceof MiningToolItem)
                && (player.getMainHandStack().getItem() instanceof SwordItem
                        || player.getMainHandStack().getItem() instanceof MiningToolItem)) {
            if (this.secondAttackCooldown <= 0) {
                if (this.crosshairTarget != null && !this.player.isRiding()) {
                    switch (this.crosshairTarget.getType()) {
                        case ENTITY:
                            // Client
                            ((PlayerAccess) player).setOffhandAttack();
                            ((PlayerAccess) player).resetLastOffhandAttackTicks();
                            player.attack(((EntityHitResult) this.crosshairTarget).getEntity());
                            // Server
                            MinecraftClient.getInstance().getNetworkHandler().sendPacket(PlayerAttackPacket
                                    .attackPacket(((EntityHitResult) this.crosshairTarget).getEntity()));
                            break;
                        case BLOCK:
                            BlockHitResult blockHitResult = (BlockHitResult) this.crosshairTarget;
                            BlockPos blockPos = blockHitResult.getBlockPos();
                            if (!player.world.getBlockState(blockPos).isAir()) {
                                this.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
                                break;
                            }
                        case MISS:
                            if (this.interactionManager.hasLimitedAttackSpeed()) {
                                this.secondAttackCooldown = 10;
                            }
                            ((PlayerAccess) player).resetLastOffhandAttackTicks();
                    }
                    attackedOffhand = true;
                    this.player.swingHand(Hand.OFF_HAND);
                    info.cancel();
                }
            } else {
                info.cancel();
            }
        } else if (this.attackedOffhand) {
            this.attackedOffhand = false;
        }
    }
}
