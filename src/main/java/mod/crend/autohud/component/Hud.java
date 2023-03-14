package mod.crend.autohud.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.lwjgl.opengl.GL30;

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
        if (player != null && MinecraftClient.getInstance().world != null) {
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

    public static void preInjectFade(Component component) {
        preInjectFade(component, 0.0f);
    }
    public static void preInjectFade(Component component, float minAlpha) {
        if (AutoHud.config.animationFade()) {
            alpha = Math.max(component.getAlpha(), minAlpha);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        }
    }
    public static void preInject(MatrixStack matrixStack, Component component) {
        if (AutoHud.config.animationFade()) {
            alpha = component.getAlpha();
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        }
        if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
            matrixStack.push();
            if (component.isHidden()) {
                matrixStack.translate(component.getDeltaX(), component.getDeltaY(), 0);
            }
        }
    }

    public static void postInjectFade() {
        if (AutoHud.config.animationFade()) {
            alpha = 1.0f;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        }
    }
    public static void postInject(MatrixStack matrixStack) {
        if (AutoHud.config.animationFade()) {
            alpha = 1.0f;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        }
        if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
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

    static Framebuffer framebuffer = null;
    static int previousFramebuffer;
    public static void prepareExtraFramebuffer() {
        // Setup extra framebuffer to draw into
        Window window = MinecraftClient.getInstance().getWindow();
        previousFramebuffer = GlStateManager.getBoundFramebuffer();
        if (framebuffer == null) {
            framebuffer = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
            framebuffer.setClearColor(0, 0, 0, 0);
        }
        framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        framebuffer.beginWrite(false);
    }

    public static void drawExtraFramebuffer(MatrixStack matrices) {
        // Restore the original framebuffer
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);

        // Render the custom framebuffer's contents with transparency into the main buffer
        RenderSystem.setShaderTexture(0, framebuffer.getColorAttachment());
        Window window = MinecraftClient.getInstance().getWindow();
        DrawableHelper.drawTexture(
                matrices,
                0,
                0,
                window.getScaledWidth(),
                window.getScaledHeight(),
                0,
                framebuffer.textureHeight,
                framebuffer.textureWidth,
                -framebuffer.textureHeight,
                framebuffer.textureWidth,
                framebuffer.textureHeight
        );
    }

    public static void resizeFramebuffer() {
        if (framebuffer != null) {
            Window window = MinecraftClient.getInstance().getWindow();
            framebuffer.resize(window.getFramebufferWidth(), window.getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
        }
    }
}
