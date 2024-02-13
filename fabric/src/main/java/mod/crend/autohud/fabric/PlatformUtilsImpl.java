package mod.crend.autohud.fabric;

import net.fabricmc.loader.api.FabricLoader;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	public static boolean isModPresent(String modid) {
		return isModLoaded(modid);
	}
}
