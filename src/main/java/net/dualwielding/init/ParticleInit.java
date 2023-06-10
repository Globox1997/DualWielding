package net.dualwielding.init;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ParticleInit {

    public static final DefaultParticleType OFFHAND_SWEEPING = FabricParticleTypes.simple(true);

    public static void initServer() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier("dualwielding", "offhand_sweeping"), OFFHAND_SWEEPING);
    }

    public static void initClient() {
        ParticleFactoryRegistry.getInstance().register(OFFHAND_SWEEPING, SweepAttackParticle.Factory::new);
    }

}
