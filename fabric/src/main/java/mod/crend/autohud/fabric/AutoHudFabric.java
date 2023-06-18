package mod.crend.autohud.fabric;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.api.AutoHudApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;

public class AutoHudFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoHud.init();

        ModKeyBindings.ALL.forEach(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(ModKeyBindings::clientTick);

        FabricLoader.getInstance().getEntrypointContainers(AutoHud.MOD_ID, AutoHudApi.class).forEach(entrypoint -> {
            AutoHud.addApi(entrypoint.getEntrypoint());
        });

    }
}
