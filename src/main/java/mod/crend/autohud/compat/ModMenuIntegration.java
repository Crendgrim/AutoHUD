//? if modmenu {
package mod.crend.autohud.compat;

import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import mod.crend.autohud.config.ConfigHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Entrypoint
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigHandler.CONFIG_STORE::makeScreen;
    }
}
//?}
