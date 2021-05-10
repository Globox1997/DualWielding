package net.dualwielding;

import net.dualwielding.init.ParticleInit;
import net.fabricmc.api.ClientModInitializer;

public class DualWieldingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleInit.initClient();
    }

}
