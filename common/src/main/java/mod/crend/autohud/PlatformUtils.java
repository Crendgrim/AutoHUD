package mod.crend.autohud;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

@SuppressWarnings("unused")
public class PlatformUtils {
	@ExpectPlatform
	public static boolean isModLoaded(String modid) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Path resolveConfigFile(String configName) {
		throw new AssertionError();
	}
}
