package mc.recraftors.configured_burn.mixin;

import mc.recraftors.configured_burn.ConfiguredBurnTime;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
    @Inject(method = "getFuelTime", at = @At("HEAD"), cancellable = true)
    private void getConfiguredFuelTimeInjector(ItemStack fuel, CallbackInfoReturnable<Integer> cir) {
        if (fuel.isEmpty()) return;
        Optional<Integer> v = ConfiguredBurnTime.getFuelTime(fuel);
        v.ifPresent(cir::setReturnValue);
    }
}
