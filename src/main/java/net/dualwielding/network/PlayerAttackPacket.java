package net.dualwielding.network;

import io.netty.buffer.Unpooled;
import net.dualwielding.access.PlayerAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.medievalweapons.init.TagInit;
import net.medievalweapons.item.Big_Axe_Item;
import net.medievalweapons.item.Long_Sword_Item;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PlayerAttackPacket {

    public static final Identifier ATTACK_PACKET = new Identifier("dualwielding", "attack_entity");

    public static Packet<?> attackPacket(Entity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        return ClientPlayNetworking.createC2SPacket(ATTACK_PACKET, buf);
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ATTACK_PACKET, (server, player, handler, buffer, sender) -> {
            ((PlayerAccess) player).setOffhandAttack();
          //  ((PlayerAccess) player).resetLastOffhandAttackTicks();
            player.updateLastActionTime();
            player.attack(player.world.getEntityById(buffer.getInt(0)));
        });

    }

    public static boolean medievalWeaponsDoubleHanded(ItemStack offHandItemStack) {
        if (FabricLoader.getInstance().isModLoaded("medievalweapons") && (
                offHandItemStack.isIn(TagInit.DOUBLE_HANDED_ITEMS)
                        || offHandItemStack.isIn(TagInit.ACCROSS_DOUBLE_HANDED_ITEMS) ||
                        offHandItemStack.getItem() instanceof Long_Sword_Item
                        || offHandItemStack.getItem() instanceof Big_Axe_Item)) {
            return false;
        } else
            return true;
    }

}
