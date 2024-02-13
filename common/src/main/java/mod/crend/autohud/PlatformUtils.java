package mod.crend.autohud;

import dev.architectury.injectables.annotations.ExpectPlatform;

@SuppressWarnings("unused")
public class PlatformUtils {
	@ExpectPlatform
	public static boolean isModLoaded(String modid) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static boolean isModPresent(String modid) {
		throw new AssertionError();
	}
}
