package mod.crend.autohud.component;

import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public class Hud {
    private static boolean dynamic = false;
    private static boolean wasPeeking = false;

    private static State state = null;

    public static boolean actDynamic() {
        return dynamic || wasPeeking;
    }

    public static void disableDynamic() {
        dynamic = false;
        Component.revealAll();
        Components.ChatIndicator.hide();
    }

    public static void enableDynamic() {
        dynamic = true;
        Component.hideAll();
    }

    public static void toggleHud() {
        if (dynamic) disableDynamic();
        else enableDynamic();
    }
    public static void peekHud(boolean doPeek) {
        if (dynamic == doPeek) {
            Component.revealAll();
            Components.ChatIndicator.hide();
        } else if (doPeek != wasPeeking) {
            Component.hideAll();
        }

        wasPeeking = doPeek;
    }

    public static void registerState(ClientPlayerEntity player) {
        state = new State(player);
        wasPeeking = false;
    }
    public static void tickState(ClientPlayerEntity player) {
        state.tick(player);
    }
    public static void unregisterState() {
        state = null;
    }

    public static boolean shouldShowIcon(StatusEffectInstance instance) {
        ComponentRenderer renderer = ComponentRenderer.getForStatusEffect(instance);
        return (!renderer.isActive() || renderer.doRender());
    }

}
