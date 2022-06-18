package mod.crend.autohud.component;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.AnimationType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
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

    public static void resetState() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        state = new State(player);
    }

    public static void tick() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (state == null) {
                state = new State(player);
                wasPeeking = false;
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

    public static boolean inRender;
    public static float alpha = 1.0f;

    public static void preInject(MatrixStack matrixStack, Component component) {
        if (AutoHud.config.animationType() == AnimationType.Fade) {
            alpha = component.getAlpha();
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        } else {
            matrixStack.push();
            if (component.isHidden()) {
                matrixStack.translate(component.getDeltaX(), component.getDeltaY(), 0);
            }
        }
    }

    public static void postInject(MatrixStack matrixStack) {
        if (AutoHud.config.animationType() == AnimationType.Fade) {
            alpha = 1.0f;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        } else {
            matrixStack.pop();
        }
    }

    /**
     * Modifies the given color to set its alpha value
     */
    public static int modifyArgb(int argb) {
        int oldAlpha = argb >> 24;
        if ((oldAlpha & 0xFC) == 0) {
            oldAlpha = 0xFF;
        }
        return Math.round(alpha * oldAlpha) << 24 | (argb & 0xFFFFFF);
    }
    public static int getArgb() {
        return Math.round(alpha * 0xFF) << 24;
    }

    public static void injectTransparency() {
        if (inRender) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        }
    }

}
