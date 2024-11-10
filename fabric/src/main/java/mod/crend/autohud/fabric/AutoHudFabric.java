package mod.crend.autohud.fabric;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.compat.HotbarSlotCyclingCompat;
import mod.crend.autohud.compat.RaisedCompat;
import mod.crend.autohud.render.AutoHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;

public class AutoHudFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoHud.init();

        ModKeyBindings.ALL.forEach(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(ModKeyBindings::clientTick);

        HudRenderCallback.EVENT.register(AutoHudRenderer::renderChatMessageIndicator);

        FabricLoader.getInstance().getEntrypointContainers(AutoHud.MOD_ID, AutoHudApi.class).forEach(entrypoint -> {
            AutoHud.addApi(entrypoint.getEntrypoint());
        });

        if (FabricLoader.getInstance().isModLoaded("raised")) {
            ClientTickEvents.END_CLIENT_TICK.register(RaisedCompat::tick);
        }

        if (FabricLoader.getInstance().isModLoaded("hotbarslotcycling")) {
            AutoHud.addApi(new HotbarSlotCyclingCompat());
        }

    }
}
