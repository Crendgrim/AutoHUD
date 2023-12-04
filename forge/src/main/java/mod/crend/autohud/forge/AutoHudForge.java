package mod.crend.autohud.forge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(AutoHud.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class AutoHudForge {
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
