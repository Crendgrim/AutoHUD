package mod.crend.autohud.gui.screen;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    public static Screen getScreen(Screen parent) {
        if (FabricLoader.getInstance().isModLoaded("yet-another-config-lib")) {
            return ConfigScreenFactory.makeScreen(parent);
        } else {
            return new NoticeScreen(
                    () -> MinecraftClient.getInstance().setScreen(parent),
                    Text.translatable("autohud.title"),
                    Text.translatable("autohud.requireYaclForConfigScreen")
            );
        }
    }

    @Override
    public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::getScreen;
    }
}
