package mod.crend.autohud.component;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;

public class Hud {
    private static boolean dynamic = false;
    private static boolean wasPeeking = false;

    private static ClientPlayerEntity player;
    private static State state = null;

    public static boolean actDynamic() {
        return dynamic || wasPeeking;
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
    public static void peekHud(boolean doPeek) {
        if (doPeek == wasPeeking) return;

        if (dynamic == doPeek) {
            Component.revealAll();
        } else {
            Component.hideAll();
        }

        wasPeeking = doPeek;
    }

    public static void tick() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (state == null) {
                state = new State(player);
                wasPeeking = false;
            } else if (player != Hud.player) {
                state.initStates(player);
                Hud.player = player;
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

    public static void preInject(MatrixStack matrixStack, Component component) {
        matrixStack.push();
        if (component.isHidden()) {
            matrixStack.translate(component.getDeltaX(), component.getDeltaY(), 0);
        }
    }

    public static void postInject(MatrixStack matrixStack) {
        matrixStack.pop();
    }
}
