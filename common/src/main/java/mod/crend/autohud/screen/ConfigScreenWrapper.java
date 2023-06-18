package mod.crend.autohud.screen;

import mod.crend.autoyacl.YaclHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreenWrapper {
	public static Screen getScreen(Screen parent) {
		if (YaclHelper.HAS_YACL) {
			return ConfigScreenFactory.makeScreen(parent);
		} else {
			return new NoticeScreen(
					() -> MinecraftClient.getInstance().setScreen(parent),
					Text.translatable("autohud.title"),
					Text.translatable("autohud.requireYaclForConfigScreen")
			);
		}
	}
}
