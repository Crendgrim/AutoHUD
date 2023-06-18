package mod.crend.autohud.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static Path resolveConfigFile(String configName) {
		return FMLPaths.CONFIGDIR.get().resolve(configName);
	}
}
