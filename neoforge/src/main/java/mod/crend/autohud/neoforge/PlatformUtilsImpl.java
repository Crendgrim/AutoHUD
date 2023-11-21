package mod.crend.autohud.neoforge;


import net.neoforged.fml.ModList;

@SuppressWarnings("unused")
public class PlatformUtilsImpl {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}
}
