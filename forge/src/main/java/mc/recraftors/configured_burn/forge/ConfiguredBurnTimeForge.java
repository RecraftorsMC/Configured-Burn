package mc.recraftors.configured_burn.forge;

import mc.recraftors.configured_burn.BurnRecipe;
import mc.recraftors.configured_burn.ConfiguredBurnTime;
import mc.recraftors.configured_burn.PreLaunchUtils;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(ConfiguredBurnTime.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ConfiguredBurnTime.MOD_ID)
public class ConfiguredBurnTimeForge {
    private static final Identifier RECIPE_TYPE_ID = ConfiguredBurnTime.modId(BurnRecipe.KEY);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPE_REGISTER = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES, ConfiguredBurnTime.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTER = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, ConfiguredBurnTime.MOD_ID);

    static {
        var id = ConfiguredBurnTime.modId(BurnRecipe.KEY);
        RECIPE_TYPE_REGISTER.register(BurnRecipe.KEY, () -> {
            BurnRecipe.init(new RecipeType<BurnRecipe>() {
                @Override
                public String toString() {
                    return id.toString();
                }
            });
            PreLaunchUtils.LOGGER.info("Configured '{}' item burn time recipe type registered", id);
            return BurnRecipe.SUPPLIER.get();
        });
        RECIPE_SERIALIZER_REGISTER.register(BurnRecipe.KEY, () -> {
            var serializer = BurnRecipe.Serializer.init();
            PreLaunchUtils.LOGGER.info("Configured '{}' item burn time recipe serializer registered", id);
            return serializer;
        });
    }

    public ConfiguredBurnTimeForge() {
        ConfiguredBurnTime.init();
        RECIPE_TYPE_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_SERIALIZER_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}