package net.dualwielding.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "swingHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V", shift = Shift.AFTER), cancellable = true)
    private void swingHandMixin(Hand hand, CallbackInfo info) {
        Item item = ((PlayerEntity) (Object) this).getOffHandStack().getItem();
        if (hand == Hand.OFF_HAND && (item instanceof SwordItem || item instanceof MiningToolItem))
            info.cancel();
    }

}
