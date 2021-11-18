package net.dualwielding.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.dualwielding.access.PlayerAccess;
import net.dualwielding.init.ParticleInit;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerAccess {

    private int lastAttackedOffhandTicks;
    @Nullable
    private Entity target;
    private boolean offHandAttack;

    @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;tick()V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;lastAttackedTicks:I", ordinal = 0))
    private void tickMixin(CallbackInfo info) {
        lastAttackedOffhandTicks++;
    }

    @Override
    public void resetLastOffhandAttackTicks() {
        lastAttackedOffhandTicks = 0;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"), ordinal = 0)
    private float attackDamageMixin(float original) {
        Item item = ((PlayerEntity) (Object) this).getOffHandStack().getItem();
        if (this.offHandAttack) {
            if (item instanceof SwordItem) {
                SwordItem swordItem = (SwordItem) item;
                return swordItem.getAttackDamage() + 1F;
            } else {
                MiningToolItem miningToolItem = (MiningToolItem) item;
                return miningToolItem.getAttackDamage() + 1F;
            }
        } else
            return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F", shift = Shift.BEFORE), ordinal = 1)
    private float attackEnchantmentDamageMixin(float original) {
        ItemStack itemStack = ((PlayerEntity) (Object) this).getOffHandStack();
        if (this.offHandAttack) {
            float h;
            if (this.target != null && this.target instanceof LivingEntity) {
                h = EnchantmentHelper.getAttackDamage(itemStack, ((LivingEntity) target).getGroup());
            } else {
                h = EnchantmentHelper.getAttackDamage(itemStack, EntityGroup.DEFAULT);
            }
            return h;
        } else
            return original;
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"), ordinal = 2)
    private float cooldownProgressMixin(float original) {
        if (this.offHandAttack) {
            return this.getAttackCooldownProgressOffhand(0.5F);
        } else
            return original;
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"))
    private void attackResetLastAttackedTickskMixin(CallbackInfo info) {
        if (this.offHandAttack) {
            this.resetLastOffhandAttackTicks();
        }
    }

    @Inject(method = "resetLastAttackedTicks", at = @At(value = "HEAD"), cancellable = true)
    private void resetLastAttackedTickskMixin(CallbackInfo info) {
        if (this.offHandAttack == true) {
            info.cancel();
        }
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;postHit(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void attackPostHitMixin(ItemStack itemstack, LivingEntity livingEntity, PlayerEntity playerEntity) {
        if (this.offHandAttack) {
            playerEntity.getOffHandStack().postHit(livingEntity, playerEntity);
        } else
            itemstack.postHit((LivingEntity) livingEntity, playerEntity);
    }

    @Inject(method = "attack", at = @At(value = "TAIL"))
    public void attackMixin(CallbackInfo info) {
        if (this.offHandAttack) {
            this.offHandAttack = false;
        }
    }

    @Inject(method = "attack", at = @At(value = "HEAD"))
    public void attackMixin(Entity target, CallbackInfo info) {
        if (target.isAttackable()) {
            this.target = target;
        }
    }

    @Inject(method = "spawnSweepAttackParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    public void spawnSweepAttackParticles(CallbackInfo info, double d, double e) {
        if (this.offHandAttack) {
            PlayerEntity playerEntity = (PlayerEntity) (Object) this;
            ((ServerWorld) playerEntity.world).spawnParticles(ParticleInit.OFFHAND_SWEEPING, playerEntity.getX() + d, playerEntity.getBodyY(0.5D), playerEntity.getZ() + e, 0, d, 0.0D, e, 0.0D);
            info.cancel();
        }
    }

    @Shadow
    public float getAttackCooldownProgressPerTick() {
        return 1.0F;
    }

    @Override
    public void setOffhandAttack() {
        this.offHandAttack = true;
    }

    @Override
    public boolean isOffhandAttack() {
        return this.offHandAttack;
    }

    @Override
    public float getAttackCooldownProgressOffhand(float baseTime) {
        return MathHelper.clamp(((float) this.lastAttackedOffhandTicks + baseTime) / this.getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

}
