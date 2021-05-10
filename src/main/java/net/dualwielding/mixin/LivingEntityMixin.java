package net.dualwielding.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.dualwielding.access.PlayerAccess;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import net.minecraft.item.Item;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.SpawnHelper;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    // @Inject(method =
    // "Lnet/minecraft/entity/LivingEntity;getHandSwingDuration()I", at =
    // @At("HEAD"), cancellable = true)
    // private void getHandSwingDuration(CallbackInfoReturnable<Integer> info) {
    // LivingEntity livingEntity = (LivingEntity) (Object) this;
    // Item item = livingEntity.getOffHandStack().getItem();
    // if(((PlayerAccess) livingEntity).isOffhandAttack()){
    // // System.out.println("JOOOOOOOOOOO");
    // }

    // // if (item.isIn(TagInit.ACCROSS_DOUBLE_HANDED_ITEMS) ||
    // // item.isIn(TagInit.DOUBLE_HANDED_ITEMS)
    // // || item instanceof Lance_Item) {
    // // info.setReturnValue(10);
    // // }
    // }
}
