package mod.crend.autohud.screen;

import dev.isxander.yacl.api.YetAnotherConfigLib;
import mod.crend.autohud.config.Config;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autoyacl.AutoYacl;
import net.minecraft.client.gui.screen.Screen;

public class ConfigScreenFactory {
	public static Screen makeScreen(Screen parent) {
		return YetAnotherConfigLib.create(ConfigHandler.CONFIG_STORE.withYacl().instance,
				(defaults, config, builder) -> AutoYacl.parse(Config.class, defaults, config, builder)
		).generateScreen(parent);
	}

}
