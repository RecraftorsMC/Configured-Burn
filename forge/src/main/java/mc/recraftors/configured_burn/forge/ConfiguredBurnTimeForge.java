package mc.recraftors.configured_burn.forge;

import mc.recraftors.configured_burn.BurnRecipe;
import mc.recraftors.configured_burn.ConfiguredBurnTime;
import mc.recraftors.configured_burn.PreLaunchUtils;
import net.minecraft.recipe.RecipeType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(ConfiguredBurnTime.MOD_ID)
public class ConfiguredBurnTimeForge {
    public ConfiguredBurnTimeForge() {
        ConfiguredBurnTime.init();
    }

    @SubscribeEvent
    public void register(RegisterEvent event) {
        var id = ConfiguredBurnTime.modId(BurnRecipe.KEY);
        event.register(ForgeRegistries.Keys.RECIPE_TYPES, helper -> {
            var type = new RecipeType<BurnRecipe>() {
                @Override
                public String toString() {
                    return id.toString();
                }
            };
            helper.register(id, type);
            BurnRecipe.init(type);
            PreLaunchUtils.LOGGER.info("Configured item burn time recipe type registered");
        });
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> {
            var serializer = BurnRecipe.Serializer.init();
            helper.register(id, serializer);
            PreLaunchUtils.LOGGER.info("Configured item burn time recipe serializer registered");
        });
    }
}