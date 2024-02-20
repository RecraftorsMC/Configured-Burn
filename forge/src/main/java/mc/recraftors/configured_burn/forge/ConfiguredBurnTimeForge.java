package mc.recraftors.configured_burn.forge;

import mc.recraftors.configured_burn.ConfiguredBurnTime;
import net.minecraftforge.fml.common.Mod;

@Mod(ConfiguredBurnTime.MOD_ID)
public class ConfiguredBurnTimeForge {
    public ConfiguredBurnTimeForge() {
        ConfiguredBurnTime.init();
    }
}