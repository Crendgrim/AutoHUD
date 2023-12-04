package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.TickEvent;

@Mod(AutoHud.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class AutoHudNeoForge {
    @SubscribeEvent
    static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ModKeyBindings.clientTick(MinecraftClient.getInstance());
            Hud.tick();
        }
    }

    @SubscribeEvent
    static void onPreRenderGuiEvent(RenderGuiEvent.Pre event) {
        AutoHudRenderer.startRender(event.getGuiGraphics(), event.getPartialTick());
    }
    @SubscribeEvent
    static void onPostRenderGuiEvent(RenderGuiEvent.Post event) {
        AutoHudRenderer.endRender();
    }
}
