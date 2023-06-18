package mod.crend.autohud.fabric.screen;

import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.crend.autohud.screen.ConfigScreenWrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreenWrapper::getScreen;
    }
}
