package mc.recraftors.configured_burn.mixin;

import mc.recraftors.configured_burn.ConfiguredBurnTime;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow public abstract MinecraftServer getServer();

    @Inject(method = "onDataPacksReloaded", at = @At("TAIL"))
    private void postDataPackReloadInjector(CallbackInfo ci) {
        ConfiguredBurnTime.loadEntries(this.getServer().getRecipeManager());
    }
}
