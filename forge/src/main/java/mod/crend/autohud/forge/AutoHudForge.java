package mod.crend.autohud.forge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
//? if raised
import mod.crend.autohud.compat.RaisedCompat;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.libbamboo.forge.ConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
//? if >=1.21.1
/*import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;*/

@Mod(AutoHud.MOD_ID)
public class AutoHudForge {
    static boolean raisedCompat = false;

    public AutoHudForge(/*? if >=1.21.1 {*//*FMLJavaModLoadingContext context*//*?}*/) {
        AutoHud.loadConfig();
        ConfigScreen.register(
                //? if <1.21.1 {
                ModLoadingContext.get(),
                //?} else
                /*context,*/
                ConfigHandler.CONFIG_STORE
        );
    }

    // Do not use @SubscribeEvent here because the mod "placebo" forces the game bus to run early for some reason.
    // This causes the key bindings to get ticked before the config is loaded...
    static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ModKeyBindings.clientTick(MinecraftClient.getInstance());

            //? if raised {
            if (raisedCompat) {
                RaisedCompat.tick();
            }
            //?}
        }
    }
}
