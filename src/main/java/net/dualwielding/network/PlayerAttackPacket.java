package net.dualwielding.network;

import io.netty.buffer.Unpooled;
import net.dualwielding.access.PlayerAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PlayerAttackPacket {

    public static final Identifier ATTACK_PACKET = new Identifier("dualwielding", "attack_entity");

    public static Packet<?> attackPacket(Entity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getEntityId());
        return ClientPlayNetworking.createC2SPacket(ATTACK_PACKET, buf);
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ATTACK_PACKET, (server, player, handler, buffer, sender) -> {
            ((PlayerAccess) player).setOffhandAttack();
          //  ((PlayerAccess) player).resetLastOffhandAttackTicks();
            player.updateLastActionTime();
            player.attack(player.world.getEntityById(buffer.getInt(0)));
            // System.out.println("PACKETWORKS");
        });

    }

}
