package mod.crend.autohud.screen;

import me.shedaniel.autoconfig.AutoConfig;
import mod.crend.autohud.config.Config;
import net.minecraft.client.gui.screen.Screen;

public class ConfigScreenFactory {
	public static Screen makeScreen(Screen parent) {
		return AutoConfig.getConfigScreen(Config.class, parent).get();
	}
}
