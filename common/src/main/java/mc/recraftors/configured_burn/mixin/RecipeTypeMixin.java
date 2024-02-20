package mc.recraftors.configured_burn.mixin;

import mc.recraftors.configured_burn.BurnRecipe;
import mc.recraftors.configured_burn.ConfiguredBurnTime;
import mc.recraftors.configured_burn.PreLaunchUtils;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeType.class)
public interface RecipeTypeMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void burnRecipeTypeRegistar(CallbackInfo ci) {
        Identifier id = ConfiguredBurnTime.modId(BurnRecipe.KEY);
        BurnRecipe.init(Registry.register(Registry.RECIPE_TYPE, id, new RecipeType<BurnRecipe>() {
            @Override
            public String toString() {
                return id.toString();
            }
        }));
        PreLaunchUtils.LOGGER.info("Configured item burn time recipe type registered");
    }
}
