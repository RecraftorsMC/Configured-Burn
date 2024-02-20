package mc.recraftors.configured_burn.mixin;

import mc.recraftors.configured_burn.ConfiguredBurnTime;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow public abstract RecipeManager getRecipeManager();

    @Inject(method = "onSynchronizeRecipes", at = @At("TAIL"))
    private void postReceiveRecipesInjection(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
        ConfiguredBurnTime.loadEntries(this.getRecipeManager());
    }
}
