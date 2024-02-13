package mod.crend.autohud.neoforge;


import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static boolean isModPresent(String modid) {
		return LoadingModList.get().getModFileById(modid) != null;
	}
}
