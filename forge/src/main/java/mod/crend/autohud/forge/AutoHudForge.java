package mod.crend.autohud.forge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.compat.RaisedCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(AutoHud.MOD_ID)
public class AutoHudForge {
    static boolean raisedCompat = false;

    // Do not use @SubscribeEvent here because the mod "placebo" forces the game bus to run early for some reason.
    // This causes the key bindings to get ticked before the config is loaded...
    static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ModKeyBindings.clientTick(MinecraftClient.getInstance());

            if (raisedCompat) {
                RaisedCompat.tick();
            }
        }
    }
}
