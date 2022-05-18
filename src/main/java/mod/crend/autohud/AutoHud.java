package mod.crend.autohud;

import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.config.Config;
import mod.crend.autohud.config.ConfigHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class AutoHud implements ClientModInitializer {

    public static Config config;
    public static final List<AutoHudApi> apis = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        ConfigHandler.init();
        config = ConfigHandler.getConfig();
        if (config.dynamicOnLoad()) {
            Hud.enableDynamic();
        }

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> Hud.render(tickDelta));

        KeyBinding toggleHudKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.autohud.toggle-hud",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,
                        "key.category.autohud"
                ));
        KeyBinding peekHudKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.autohud.peek-hud",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        "key.category.autohud"
                ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKeyBinding.wasPressed()) {
                Hud.toggleHud();
            }
            Hud.peekHud(peekHudKeyBinding.isPressed());
        });

        FabricLoader.getInstance().getEntrypointContainers("autohud", AutoHudApi.class).forEach(entrypoint -> {
            apis.add(entrypoint.getEntrypoint());
        });

    }
}
