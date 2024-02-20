package mc.recraftors.configured_burn.mixin;

import mc.recraftors.configured_burn.BurnRecipe;
import mc.recraftors.configured_burn.ConfiguredBurnTime;
import mc.recraftors.configured_burn.PreLaunchUtils;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeSerializer.class)
public interface RecipeSerializerMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void burnRecipeSerializerRegistar(CallbackInfo ci) {
        Registry.register(Registries.RECIPE_SERIALIZER, ConfiguredBurnTime.modId("burning_time"), BurnRecipe.Serializer.init());
        PreLaunchUtils.LOGGER.info("Configured item burn time recipe serializer registered");
    }
}
