package mc.recraftors.configured_burn.mixin;

import mc.recraftors.configured_burn.ConfiguredBurnTime;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeEventFactory.class)
public abstract class ForgeEventFactoryMixin {
    @Inject(method = "getItemBurnTime", at = @At("TAIL"), cancellable = true)
    private static void getConfiguredFuelTimeInjector(
            @NotNull ItemStack itemStack, int burnTime, @Nullable RecipeType<?> recipeType,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (itemStack.isEmpty()) return;
        ConfiguredBurnTime.getFuelTime(itemStack).ifPresent(cir::setReturnValue);
    }
}
