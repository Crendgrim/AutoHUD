package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.compat.RaisedCompat;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@Mod(AutoHud.MOD_ID)
@EventBusSubscriber(value = Dist.CLIENT)
public class AutoHudNeoForge {
    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        ModKeyBindings.clientTick(MinecraftClient.getInstance());

        if (ModList.get().isLoaded("raised")) {
            RaisedCompat.tick();
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

    @SubscribeEvent
    static void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (Component.Chat.config.active() && Component.ChatIndicator.config.active() && Component.Chat.isHidden()) {
            Component.ChatIndicator.reveal();
        }
    }

}
