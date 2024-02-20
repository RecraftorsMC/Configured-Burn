package mc.recraftors.configured_burn.fabric;

import mc.recraftors.configured_burn.ConfiguredBurnTime;
import net.fabricmc.api.ModInitializer;

public class ConfiguredBurnTimeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfiguredBurnTime.init();
    }
}