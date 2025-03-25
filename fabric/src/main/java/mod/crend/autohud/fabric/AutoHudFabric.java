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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

//? if <=1.21.4 {
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
*///?}

public class AutoHudFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoHud.loadConfig();
        AutoHud.init();

        ModKeyBindings.ALL.forEach(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(ModKeyBindings::clientTick);

        //? if <=1.21.4 {
        HudRenderCallback.EVENT.register(AutoHudRenderer::renderChatMessageIndicator);
        //?} else {
        /*HudLayerRegistrationCallback.EVENT.register(event ->
            event.addLayer(IdentifiedLayer.of(Identifier.of(AutoHud.MOD_ID, "chat_indicator"), AutoHudRenderer::renderChatMessageIndicator))
        );
        *///?}

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
