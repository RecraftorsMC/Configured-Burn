package mc.recraftors.configured_burn.mixin.compat.fabric_content_registries.present;

import mc.recraftors.configured_burn.ConfiguredBurnTime;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = FuelRegistryImpl.class, remap = false)
public abstract class FuelRegistryImplMixin {
    @Redirect(method = "getFuelTimes", at = @At(value = "INVOKE", target = "Ljava/util/IdentityHashMap;<init>(Ljava/util/Map;)V"))
    private void reconfigureFuelRegistryRedirector(IdentityHashMap<Item, Integer> instance, Map<Item, Integer> map) {
        ConfiguredBurnTime.updateFuelMap(instance);
    }
}
