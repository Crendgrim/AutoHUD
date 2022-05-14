package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.hit.HitResult;

public class Hud {
    private static boolean dynamic = true;

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

    public static boolean shouldShowCrosshair() {
        if (!AutoHud.config.dynamicCrosshair()) return true;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        Item mainHandItem = player.getMainHandStack().getItem();
        if (mainHandItem instanceof ToolItem) return true;
        if (mainHandItem instanceof RangedWeaponItem) return true;
        if (mainHandItem instanceof TridentItem) return true;
        if (mainHandItem instanceof EggItem) return true;
        if (mainHandItem instanceof SnowballItem) return true;
        if (mainHandItem instanceof ThrowablePotionItem) return true;

        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (hitResult == null) {
            return false;
        }
        return hitResult.getType() == HitResult.Type.ENTITY;
    }
}
