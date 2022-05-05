package mod.crend.autohud.component;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class Hud {
    private static boolean dynamic = false;

    private static State state = null;

    public static boolean isDynamic() {
        return dynamic;
    }

    public static void disableDynamic() {
        dynamic = false;
        Component.revealAll();
    }

    public static void enableDynamic() {
        dynamic = true;
        Component.hideAll();
    }

    public static void toggleHud() {
        if (dynamic) disableDynamic();
        else enableDynamic();
    }

    public static void tick() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (state == null) {
                state = new State(player);
            }
            state.tick();
        } else {
            state = null;
        }
    }

    public static void render(float tickDelta) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (state == null) {
                state = new State(player);
            }
            state.render(player, tickDelta);
        } else {
            state = null;
        }
    }
}
