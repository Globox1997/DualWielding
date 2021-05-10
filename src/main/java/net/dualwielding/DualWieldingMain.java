package net.dualwielding;

import net.dualwielding.init.ParticleInit;
import net.dualwielding.network.PlayerAttackPacket;
import net.fabricmc.api.ModInitializer;

public class DualWieldingMain implements ModInitializer {

    @Override
    public void onInitialize() {
        ParticleInit.initServer();
        PlayerAttackPacket.init();
    }

}
