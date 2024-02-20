package mc.recraftors.configured_burn;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

public final class PreLaunchUtils {
    public static final Logger LOGGER = LogManager.getLogger(ConfiguredBurnTime.MOD_ID);

    private PreLaunchUtils() {}

    @Contract
    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new UnsupportedOperationException();
    }

    @Contract
    @ExpectPlatform
    public static boolean modHasAuthor(String modId, String author) {
        throw new UnsupportedOperationException();
    }
}
