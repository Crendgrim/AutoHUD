package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
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
}
