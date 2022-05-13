package mod.crend.autohud.component;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

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
            state.tick(player);
        } else {
            state = null;
        }
    }

    public static void render(float tickDelta) {
        if (state != null) {
            state.render(tickDelta);
        }
    }

    public static boolean shouldShowIcon(StatusEffectInstance instance) {
        if (instance.shouldShowIcon()) {
            Component component = Component.get(instance.getEffectType());
            return (!component.fullyHidden());
        }
        return false;
    }
}
