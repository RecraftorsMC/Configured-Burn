package mc.recraftors.configured_burn.mixin.compat.create.present;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import mc.recraftors.configured_burn.BurnRecipe;
import mc.recraftors.configured_burn.ConfiguredBurnTime;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = BlazeBurnerBlockEntity.class, remap = false)
public abstract class BlazeBurnerBlockEntityMixin {
    @Redirect(method = "tryUpdateFuel", at = @At(value = "CONSTANT", args = "intValue=3200"))
    private int customOverheatTimeInjector(ItemStack itemStack, boolean forceOverflow, TransactionContext ctx) {
        return ConfiguredBurnTime.getFuelEntry(itemStack).map(entry -> {
            BurnRecipe.CompatBurnTime compat = entry.compat();
            if (compat == BurnRecipe.CompatBurnTime.DEFAULT || compat.getCreateOverheatTime() < 0) return 3200;
            return compat.getCreateOverheatTime();
        }).orElse(3200);
    }

    @Redirect(method = "tryUpdateFuel", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllTags$AllItemTags;matches(Lnet/minecraft/item/ItemStack;)Z", opcode = 0))
    private boolean doesConfiguredOverheatInjector(AllTags.AllItemTags instance, ItemStack stack){
        Optional<ConfiguredBurnTime.CustomFuelEntry> entry = ConfiguredBurnTime.getFuelEntry(stack);
        if (entry.isPresent()) {
            BurnRecipe.CompatBurnTime compat = entry.get().compat();
            if (compat != BurnRecipe.CompatBurnTime.DEFAULT && compat.getCreateOverheatTime() >= 0) {
                return compat.getCreateOverheatTime() > 0;
            }
        }
        return instance.matches(stack);
    }
}
