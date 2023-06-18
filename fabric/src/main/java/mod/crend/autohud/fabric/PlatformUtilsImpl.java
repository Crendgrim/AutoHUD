package mod.crend.autohud.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	public static Path resolveConfigFile(String configName) {
		return FabricLoader.getInstance().getConfigDir().resolve(configName);
	}
}
